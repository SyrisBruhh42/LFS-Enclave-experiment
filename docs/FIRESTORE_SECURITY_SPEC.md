# Security Specification: Enclave 16 Firestore Rules

## 1. Data Invariants
- **Ownership Invariant**: Every document in `/users/{userId}/enclave_saves/{saveId}` belongs strictly to `userId`. Only an authenticated user with `request.auth.uid == userId` may read, list, create, update, or delete their own save records.
- **Identity Field Integrity**: `incoming().userId` must equal `request.auth.uid` and `userId` path parameter on create, and remain immutable (`incoming().userId == existing().userId`) on update.
- **Temporal Integrity**: `createdAt` and `updatedAt` must be valid Firestore `timestamp` values (`<= request.time`). On update, `createdAt` is strictly immutable (`incoming().createdAt == existing().createdAt`).
- **Schema & Size Bounds**: Every string field enforces strict `.size()` bounds and every integer field enforces domain range bounds (`dayNumber >= 1`, `colonyMorale` 0..100, `magneticFlux` 0..100).

## 2. The "Dirty Dozen" Adversarial Payloads
1. **Unauthenticated Read**: `unauthenticatedContext` attempting `get` or `list` on `/users/alice_123/enclave_saves`.
2. **Cross-User Read**: `bob_456` attempting `get` or `list` on `/users/alice_123/enclave_saves/save_1`.
3. **Cross-User Write**: `bob_456` attempting `create` under `/users/alice_123/enclave_saves/save_1`.
4. **Identity Spoofing in Body**: `alice_123` creating `/users/alice_123/enclave_saves/save_1` with `userId: "bob_456"`.
5. **Shadow / Ghost Field Injection (Create)**: Including `isAdmin: true` or `hacked: "yes"` on create.
6. **Shadow / Ghost Field Injection (Update)**: Including `extraField: 123` on update.
7. **Immutable Field Mutation**: Attempting to mutate `createdAt` or `userId` during an `update`.
8. **Type Poisoning**: Passing `dayNumber: "one"` (string instead of int) or `colonyMorale: true`.
9. **Range Boundary Violation**: Passing `colonyMorale: 999` or `dayNumber: 0`.
10. **String Oversize DoW Attack**: Passing `lastSummary` > 2000 chars or empty `activePovId: ""`.
11. **Timestamp Spoofing**: Passing a future timestamp (`request.time + 1 day`) or numeric epoch for `createdAt`.
12. **Path ID Poisoning**: Attempting to create a document with invalid characters or >128 chars in `saveId`.
