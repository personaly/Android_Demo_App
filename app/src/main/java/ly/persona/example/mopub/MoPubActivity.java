package ly.persona.example.mopub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;

import com.mopub.common.MoPubReward;
import com.mopub.mobileads.CustomEventRewardedVideo;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.PersonalyMediationSettings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ly.persona.example.BaseActivity;
import ly.persona.example.R;
import ly.persona.sdk.Personaly;
import ly.persona.sdk.model.Field;

import static ly.persona.example.Keys.moPubInterstitialAdUnitId;
import static ly.persona.example.Keys.moPubNativeAdUnitId;
import static ly.persona.example.Keys.moPubRewardedVideoAdUnitId;

/**
 * Created by Oleg Tarashkevich on 09/11/2017.
 */

public class MoPubActivity extends BaseActivity implements MoPubInterstitial.InterstitialAdListener, MoPubRewardedVideoListener {

    // Interstitial
    @BindView(R.id.mopub_int_placement_input_layout) TextInputLayout placementIdIntInputLayout;
    @BindView(R.id.mopub_int_placement_edit_text) EditText placementIdIntEditText;
    @BindView(R.id.mopub_int_load_button) Button loadInterstitialButton;
    @BindView(R.id.mopub_int_show_button) Button showInterstitialButton;

    // Rewarded
    @BindView(R.id.mopub_rew_placement_input_layout) TextInputLayout placementIdRewInputLayout;
    @BindView(R.id.mopub_rew_placement_edit_text) EditText placementIdRewEditText;
    @BindView(R.id.mopub_rew_load_button) Button loadRewardedButton;
    @BindView(R.id.mopub_rew_show_button) Button showRewardedButton;

    // Native
    @BindView(R.id.mopub_nat_placement_input_layout) TextInputLayout placementIdNatInputLayout;
    @BindView(R.id.mopub_nat_placement_edit_text) EditText placementIdNatEditText;
    @BindView(R.id.mopub_nat_recycler_button) Button showNativeButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub);
        ButterKnife.bind(this);

        // disable autocaching
        Personaly.setAutoCaching(false);

        setTitle("Persona.ly - MoPub adapters");
        placementIdIntEditText.setText(moPubInterstitialAdUnitId);
        placementIdRewEditText.setText(moPubRewardedVideoAdUnitId);
        placementIdNatEditText.setText(moPubNativeAdUnitId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyInterstitial();
    }

    // region MoPub Interstitial
    private MoPubInterstitial mMoPubInterstitial;

    private void destroyInterstitial() {
        if (mMoPubInterstitial != null) {
            mMoPubInterstitial.destroy();
            mMoPubInterstitial = null;
        }
    }

    @OnClick(R.id.mopub_int_load_button)
    public void onInterstitialLoadClick() {
        showToast("Interstitial start loading");
        showProgressBar();

        showInterstitialButton.setEnabled(false);
        if (mMoPubInterstitial == null) {
            moPubInterstitialAdUnitId = checkInputLayout(true, placementIdIntInputLayout, placementIdIntEditText);
            mMoPubInterstitial = new MoPubInterstitial(this, moPubInterstitialAdUnitId);
            mMoPubInterstitial.setInterstitialAdListener(this);

            // Pass Server Params
            final Map<String, Object> params = new HashMap<>();
            params.put(Field.CLICK_ID, getCustomParameter());

            mMoPubInterstitial.setLocalExtras(params);
        }

        mMoPubInterstitial.load();
    }

    @OnClick(R.id.mopub_int_show_button)
    public void onInterstitialShowClick() {
        if (mMoPubInterstitial != null) {
            mMoPubInterstitial.show();
        }
    }

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        hideProgressBar();
        showInterstitialButton.setEnabled(true);
        showToast("Interstitial loaded.");
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        hideProgressBar();
        showInterstitialButton.setEnabled(false);
        final String errorMessage = (errorCode != null) ? errorCode.toString() : "";
        showToast("Interstitial failed to load: " + errorMessage);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        showInterstitialButton.setEnabled(false);
        showToast("Interstitial shown.");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        showToast("Interstitial clicked.");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        showToast("Interstitial dismissed.");
    }
    // endregion

    // region MoPub Rewarded
    private boolean sRewardedVideoInitialized;

    private void initRewardedVideo() {

        if (!sRewardedVideoInitialized) {
            final List<Class<? extends CustomEventRewardedVideo>> classes = new LinkedList<>();
            classes.add(CustomEventRewardedVideo.class);

            // Pass Server Params
            PersonalyMediationSettings settings = new PersonalyMediationSettings(getCustomParameter());

            MoPubRewardedVideos.initializeRewardedVideo(this, classes, settings);
            sRewardedVideoInitialized = true;
        }
        MoPubRewardedVideos.setRewardedVideoListener(this);
    }

    @OnClick(R.id.mopub_rew_load_button)
    public void onRewardedLoadClick() {
        initRewardedVideo();
        showProgressBar();
        showToast("Rewarded start loading");
        moPubRewardedVideoAdUnitId = checkInputLayout(true, placementIdRewInputLayout, placementIdRewEditText);
        MoPubRewardedVideos.loadRewardedVideo(moPubRewardedVideoAdUnitId,
                new MoPubRewardedVideoManager.RequestParameters(null, null, "sample_app_customer_id"));
    }

    @OnClick(R.id.mopub_rew_show_button)
    public void onRewardedShowClick() {
        MoPubRewardedVideos.showRewardedVideo(moPubRewardedVideoAdUnitId);
    }

    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        hideProgressBar();
        showToast("Rewarded ad loaded");
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        hideProgressBar();
        showToast("Rewarded ad failed: " + errorCode.toString());
    }

    @Override
    public void onRewardedVideoStarted(@NonNull String adUnitId) {
        showToast("Rewarded ad started");
    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        showToast("onRewardedVideoPlaybackError");
    }

    @Override
    public void onRewardedVideoClicked(@NonNull String adUnitId) {
        showToast("Rewarded ad clicked");
    }

    @Override
    public void onRewardedVideoClosed(@NonNull String adUnitId) {
        showToast("Rewarded ad closed");
    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
        showToast(String.format(Locale.US,
                "Rewarded video completed with reward  \"%d %s\"",
                reward.getAmount(),
                reward.getLabel()));
    }
    // endregion

    // region MoPub Native
    @OnClick(R.id.mopub_nat_recycler_button)
    public void onNativeShowInRecyclerViewClick() {
        moPubNativeAdUnitId = checkInputLayout(true, placementIdNatInputLayout, placementIdNatEditText);
        Intent intent = new Intent(this, MoPubNativeRecyclerViewActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.mopub_nat_list_button)
    public void onNativeShowInListViewClick() {
        moPubNativeAdUnitId = checkInputLayout(true, placementIdNatInputLayout, placementIdNatEditText);
        Intent intent = new Intent(this, MoPubNativeListViewActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.mopub_nat_view_button)
    public void onNativeViewShowClick() {
        moPubNativeAdUnitId = checkInputLayout(true, placementIdNatInputLayout, placementIdNatEditText);
        Intent intent = new Intent(this, MoPubNativeViewActivity.class);
        intent.putExtra(Field.CLICK_ID, getCustomParameter());
        startActivity(intent);
    }
    // endregion
}
