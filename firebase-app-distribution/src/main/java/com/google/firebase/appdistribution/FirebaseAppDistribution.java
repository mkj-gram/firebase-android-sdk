// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.appdistribution;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FirebaseAppDistribution implements Application.ActivityLifecycleCallbacks{

  private final FirebaseApp firebaseApp;
  private static final String TAG = "FirebaseAppDistribution";
  private Activity currentActivity;

  /** Constructor for FirebaseAppDistribution */
  public FirebaseAppDistribution(FirebaseApp firebaseApp) {
    this.firebaseApp = firebaseApp;
  }

  /** @return a FirebaseAppDistribution instance */
  @NonNull
  public static FirebaseAppDistribution getInstance() {
    return getInstance(FirebaseApp.getInstance());
  }

  /**
   * Returns the {@link FirebaseAppDistribution} initialized with a custom {@link FirebaseApp}.
   *
   * @param app a custom {@link FirebaseApp}
   * @return a {@link FirebaseAppDistribution} instance
   */
  @NonNull
  public static FirebaseAppDistribution getInstance(@NonNull FirebaseApp app) {
    Preconditions.checkArgument(app != null, "Null is not a valid value of FirebaseApp.");
    return (FirebaseAppDistribution) app.get(FirebaseAppDistribution.class);
  }


  /**
   * Updates the app to the latest release, if one is available. Returns the release information or
   * null if no update is found. Performs the following actions: 1. If tester is not signed in,
   * presents the tester with a Google sign in UI 2. Checks if a newer release is available. If so,
   * presents the tester with a confirmation dialog to begin the download. 3. For APKs, downloads
   * the binary and starts an installation intent. 4. For AABs, directs the tester to the Play app
   * to complete the download and installation.
   */
  @NonNull
  public Task<AppDistributionRelease> updateToLatestRelease() {
    Log.v("updateToLatestRelease", "called");
    Log.v("updateToLatestRelease", currentActivity.getClass().getName());

    TaskCompletionSource<AppDistributionRelease> taskCompletionSource = new TaskCompletionSource<>();

    AlertDialog alertDialog = new AlertDialog.Builder(currentActivity).create();
    alertDialog.setTitle("Get New Build Alerts");
    alertDialog.setMessage("Do you want to sign-in for new build alerts?");
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sign-in", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        taskCompletionSource.setResult(null);
      }
    });
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        taskCompletionSource.setException(new FirebaseAppDistributionException(
                FirebaseAppDistributionException.AUTHENTICATION_CANCELED_ERROR));
        dialogInterface.dismiss();
      }
    });
    alertDialog.show();

    return taskCompletionSource.getTask();
  }

  /**
   * Returns an AppDistributionRelease if one is available for the current signed in tester. If no
   * update is found, returns null. If tester is not signed in, presents the tester with a Google
   * sign in UI
   */
  @NonNull
  public Task<AppDistributionRelease> checkForUpdate() {
    return Tasks.forResult(null);
  }

  /**
   * Updates app to the latest release. If the latest release is an APK, downloads the binary and
   * starts an installation If the latest release is an AAB, directs the tester to the Play app to
   * complete the download and installation.
   *
   * @throws FirebaseAppDistributionException with UPDATE_NOT_AVAIALBLE exception if no new release
   * is cached from checkForUpdate
   * @param updateProgressListener a callback function invoked as the update progresses
   */
  @NonNull
  public Task<Void> updateApp(@Nullable UpdateProgressListener updateProgressListener) {
    return Tasks.forResult(null);
  }

  /** Signs in the App Distribution tester. Presents the tester with a Google sign in UI */
  @NonNull
  public Task<Void> signInTester() {
    return Tasks.forResult(null);
  }

  /** Returns true if the App Distribution tester is signed in */
  @NonNull
  public boolean isTesterSignedIn() {
    return false;
  }

  /** Signs out the App Distribution tester */
  public void signOutTester() {}

  @Override
  public void onActivityCreated(@NonNull @NotNull Activity activity, @androidx.annotation.Nullable @Nullable Bundle bundle) {
    Log.d(TAG, "Created activity: " + activity.getClass().getName());
  }

  @Override
  public void onActivityStarted(@NonNull @NotNull Activity activity) {
    Log.d(TAG, "Started activity: " + activity.getClass().getName());
  }

  @Override
  public void onActivityResumed(@NonNull @NotNull Activity activity) {
    Log.d(TAG, "Resumed activity: " + activity.getClass().getName());
    this.currentActivity = activity;
  }

  @Override
  public void onActivityPaused(@NonNull @NotNull Activity activity) {
    Log.d(TAG, "Paused activity: " + activity.getClass().getName());
  }

  @Override
  public void onActivityStopped(@NonNull @NotNull Activity activity) {
    Log.d(TAG, "Stopped activity: " + activity.getClass().getName());
  }

  @Override
  public void onActivitySaveInstanceState(@NonNull @NotNull Activity activity, @NonNull @NotNull Bundle bundle) {
    Log.d(TAG, "Saved activity: " + activity.getClass().getName());
  }

  @Override
  public void onActivityDestroyed(@NonNull @NotNull Activity activity) {
    Log.d(TAG, "Destroyed activity: " + activity.getClass().getName());
    this.currentActivity = null;
  }
}