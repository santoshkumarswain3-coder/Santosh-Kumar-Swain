const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_uid_1";
const BOB_UID = "bob_uid_2";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

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

function validUserProfile(uid, ts) {
  return {
    userId: uid,
    country: "India",
    phoneNumber: "+919876543210",
    email: "alice@example.com",
    studentName: "Aarav Sharma",
    schoolName: "Sunrise Public School",
    coins: 100,
    dailyAskCount: 2,
    weeklyAskCount: 5,
    monthlyAskCount: 10,
    askDayKey: "2026-11-20",
    askWeekKey: "2026-W47",
    askMonthKey: "2026-11",
    dailyBonusDayKey: "",
    weeklyBonusWeekKey: "",
    monthlyBonusMonthKey: "",
    createdAt: ts,
    updatedAt: ts,
  };
}

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const ts = new Date(Date.now() - 1000);
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set(validUserProfile(ALICE_UID, ts)));
  await assertFails(unauthDb.collection("leaderboard").where("publicProfile", "==", true).get());
  await assertFails(unauthDb.collection("questions").where("published", "==", true).get());
});

test("User profile PII isolation: owner can create/update, cross-user read/write fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validUserProfile(ALICE_UID, ts))
  );

  // Partial update allowed
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).update({
      coins: 110,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Ghost field & immutable createdAt rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Cross-user PII read/write rejected
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({ coins: 0, updatedAt: ts }));
});

test("Leaderboard public non-PII query succeeds with filter, rejects unfiltered or forged owner", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("leaderboard").doc(ALICE_UID).set({
      userId: ALICE_UID,
      studentName: "Aarav Sharma",
      schoolName: "Sunrise Public School",
      coins: 100,
      levelNumber: 2,
      levelTitle: "Good",
      publicProfile: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Filtered list succeeds
  await assertSucceeds(bobDb.collection("leaderboard").where("publicProfile", "==", true).get());
  // Unfiltered list fails
  await assertFails(bobDb.collection("leaderboard").get());
  // Bob cannot modify Alice's leaderboard entry
  await assertFails(bobDb.collection("leaderboard").doc(ALICE_UID).update({ coins: 999999, updatedAt: ts }));
});

test("Test history subcollection: owner create/list succeeds, update blocked, cross-user denied", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("history").doc("attempt_1").set({
      userId: ALICE_UID,
      testType: "DAILY",
      title: "Daily GK Challenge",
      totalQuestions: 10,
      correctAnswers: 8,
      coinsEarned: 10,
      passed: true,
      studentName: "Aarav Sharma",
      schoolName: "Sunrise Public School",
      durationSeconds: 42,
      createdAt: ts,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("history").get());
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("history").doc("attempt_1").update({ correctAnswers: 10 })
  );
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("history").get());
});

test("Shared questions: create & filtered list succeed, @DocumentId id key rejected, non-author edit fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("questions").doc("q_1").set({
      authorId: ALICE_UID,
      authorName: "Aarav Sharma",
      category: "CURRENT_AFFAIRS",
      questionText: "Which planet is known as the Red Planet?",
      options: ["Venus", "Mars", "Jupiter", "Saturn"],
      correctIndex: 1,
      explanation: "Iron oxide dust on Mars gives it a reddish look.",
      published: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Rejects explicit 'id' field
  await assertFails(
    aliceDb.collection("questions").doc("q_2").set({
      id: "q_2",
      authorId: ALICE_UID,
      authorName: "Aarav Sharma",
      category: "GENERAL",
      questionText: "How many continents are on Earth?",
      options: ["5", "6", "7", "8"],
      correctIndex: 2,
      explanation: "There are 7 continents.",
      published: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(bobDb.collection("questions").where("published", "==", true).get());
  await assertFails(bobDb.collection("questions").get());
  await assertFails(bobDb.collection("questions").doc("q_1").update({ questionText: "Tampered", updatedAt: ts }));
});

test("Friend requests: sender creates pending, only recipient accepts once, arrayContains list works", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection("friendRequests").doc("req_alice_bob").set({
      fromUid: ALICE_UID,
      fromName: "Aarav Sharma",
      fromSchool: "Sunrise Public School",
      fromCoins: 100,
      toUid: BOB_UID,
      toName: "Diya Patel",
      toSchool: "Greenwood Academy",
      toCoins: 250,
      participants: [ALICE_UID, BOB_UID],
      status: "pending",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  // Sender cannot accept own request
  await assertFails(
    aliceDb.collection("friendRequests").doc("req_alice_bob").update({
      status: "accepted",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Recipient can accept
  await assertSucceeds(
    bobDb.collection("friendRequests").doc("req_alice_bob").update({
      status: "accepted",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Terminal state locked: cannot revert accepted -> pending
  await assertFails(
    bobDb.collection("friendRequests").doc("req_alice_bob").update({
      status: "pending",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  await assertSucceeds(
    aliceDb.collection("friendRequests").where("participants", "array-contains", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("friendRequests").get());
});
