const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function validSavePayload(uid, overrides = {}) {
  const now = new Date();
  return {
    userId: uid,
    dayNumber: 1,
    relayStep: 1,
    activePovId: "silas",
    currentBiome: "arboretum",
    colonyMorale: 85,
    magneticFlux: 40,
    gatesTraversed: 0,
    statePayloadJson: "{\"version\":1}",
    lastSummary: "Silas inspected the Fe-Co-Cr-Ni-Mn high-entropy alloy pipe in the arboretum.",
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("1. Unauthenticated user cannot read or write enclave saves", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).collection("enclave_saves").get()
  );
  await assertFails(
    unauthDb
      .collection("users")
      .doc(ALICE_UID)
      .collection("enclave_saves")
      .doc("primary_save")
      .set(validSavePayload(ALICE_UID))
  );
});

test("2. Authenticated owner can create, get, list, update, and delete their own enclave save", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("enclave_saves")
    .doc("primary_save");

  const initialPayload = validSavePayload(ALICE_UID);
  await assertSucceeds(docRef.set(initialPayload));
  await assertSucceeds(docRef.get());
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("enclave_saves").get()
  );

  await assertSucceeds(
    docRef.update({
      dayNumber: 2,
      relayStep: 2,
      activePovId: "kaio",
      gatesTraversed: 1,
      lastSummary: "Kaio walked through the Bioluminescent Syntax Gate.",
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(docRef.delete());
});

test("3. Cross-user isolation: Bob cannot read, list, update, or delete Alice's save", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context
      .firestore()
      .collection("users")
      .doc(ALICE_UID)
      .collection("enclave_saves")
      .doc("primary_save")
      .set(validSavePayload(ALICE_UID));
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const aliceDocFromBob = bobDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("enclave_saves")
    .doc("primary_save");

  await assertFails(aliceDocFromBob.get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).collection("enclave_saves").get()
  );
  await assertFails(
    aliceDocFromBob.update({
      colonyMorale: 10,
      updatedAt: new Date(),
    })
  );
  await assertFails(aliceDocFromBob.delete());
});

test("4. Shadow update / ghost field injection is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("enclave_saves")
    .doc("primary_save");

  await assertFails(
    docRef.set(validSavePayload(ALICE_UID, { isVerifiedAdmin: true }))
  );

  await assertSucceeds(docRef.set(validSavePayload(ALICE_UID)));
  await assertFails(
    docRef.update({
      ghostField: "malicious",
      updatedAt: new Date(),
    })
  );
});

test("5. Value poisoning and out-of-range metrics are rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb
    .collection("users")
    .doc(ALICE_UID)
    .collection("enclave_saves")
    .doc("primary_save");

  await assertFails(
    docRef.set(validSavePayload(ALICE_UID, { colonyMorale: 150 }))
  );
  await assertFails(
    docRef.set(validSavePayload(ALICE_UID, { dayNumber: 0 }))
  );
  await assertFails(
    docRef.set(validSavePayload(ALICE_UID, { userId: BOB_UID }))
  );
});
