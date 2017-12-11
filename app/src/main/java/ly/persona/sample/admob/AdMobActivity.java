package ly.persona.sample.admob;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.Button;
import android.widget.EditText;

import com.google.ads.mediation.personaly.PersonalyInterstitial;
import com.google.ads.mediation.personaly.PersonalyRewarded;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.mediation.MediationAdapter;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ly.persona.sample.BaseActivity;
import ly.persona.sample.R;
import ly.persona.sdk.Config;
import ly.persona.sdk.Personaly;
import ly.persona.sdk.model.Field;
import ly.persona.sdk.model.Gender;

import static ly.persona.sample.Keys.adMobInterstitialAdUnitId;
import static ly.persona.sample.Keys.adMobNativeAdUnitId;
import static ly.persona.sample.Keys.adMobRewardedVideoAdUnitId;
import static ly.persona.sample.Keys.moPubNativeAdUnitId;

/**
 * Created by Oleg Tarashkevich on 09/11/2017.
 */

public class AdMobActivity extends BaseActivity {

    // Interstitial
    @BindView(R.id.admob_int_placement_input_layout) TextInputLayout placementIdIntInputLayout;
    @BindView(R.id.admob_int_placement_edit_text) EditText placementIdIntEditText;
    @BindView(R.id.admob_int_load_button) Button loadInterstitialButton;
    @BindView(R.id.admob_int_show_button) Button showInterstitialButton;

    // Rewarded
    @BindView(R.id.admob_rew_placement_input_layout) TextInputLayout placementIdRewInputLayout;
    @BindView(R.id.admob_rew_placement_edit_text) EditText placementIdRewEditText;
    @BindView(R.id.admob_rew_load_button) Button loadRewardedButton;
    @BindView(R.id.admob_rew_show_button) Button showRewardedButton;

    // Native
    @BindView(R.id.admob_nat_placement_input_layout) TextInputLayout placementIdNatInputLayout;
    @BindView(R.id.admob_nat_placement_edit_text) EditText placementIdNatEditText;
    @BindView(R.id.admob_nat_show_button) Button showNativeButton;

    private InterstitialAd interstitialAd;
    private RewardedVideoAd rewardedVideoAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admob);
        ButterKnife.bind(this);

        // disable autocaching
        Personaly.setAutoCaching(false);

        setTitle("Persona.ly - AdMob adapters");
        placementIdIntEditText.setText(adMobInterstitialAdUnitId);
        placementIdRewEditText.setText(adMobInterstitialAdUnitId);
        placementIdNatEditText.setText(adMobNativeAdUnitId);
    }

    // region AdMob Interstitial
    @OnClick(R.id.admob_int_load_button)
    public void onInterstitialLoadClick() {
        showToast("Interstitial start loading");
        showProgressBar();
        showInterstitialButton.setEnabled(false);
        interstitial();
    }

    @OnClick(R.id.admob_int_show_button)
    public void onInterstitialShowClick() {
        if (interstitialAd != null && interstitialAd.isLoaded())
            interstitialAd.show();
    }

    public void interstitial() {
        adMobInterstitialAdUnitId = checkInputLayout(true, placementIdRewInputLayout, placementIdRewEditText);
        interstitialAd = new InterstitialAd(this);
//        interstitialAd.setAdUnitId("ca-app-pub-3448655774227075/1595720546");  // sample
        interstitialAd.setAdUnitId(adMobInterstitialAdUnitId);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                hideProgressBar();
                showInterstitialButton.setEnabled(true);
                showToast("Error loading interstitial interstitial, code " + errorCode);
            }

            @Override
            public void onAdLoaded() {
                hideProgressBar();
                showInterstitialButton.setEnabled(true);
                showToast("interstitial onAdLoaded");
            }

            @Override
            public void onAdOpened() {
                showInterstitialButton.setEnabled(false);
                showToast("interstitial onAdOpened");
            }

            @Override
            public void onAdClosed() {
//                interstitialAd.loadAd(getEventAdRequest(PersonalyInterstitial.class));
                showToast("interstitial onAdClosed");
            }
        });
        interstitialAd.loadAd(getEventAdRequest(PersonalyInterstitial.class));
    }
    // endregion

    // region AdMob Rewarded
    @OnClick(R.id.admob_rew_load_button)
    public void onRewardedLoadClick() {
        showProgressBar();
        showRewardedButton.setEnabled(false);
        showToast("Rewarded start loading");
        rewarded();
    }

    @OnClick(R.id.admob_rew_show_button)
    public void onRewardedShowClick() {
        if (rewardedVideoAd != null && rewardedVideoAd.isLoaded())
            rewardedVideoAd.show();
    }

    private void rewarded() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                hideProgressBar();
                showRewardedButton.setEnabled(true);
                showToast("Rewarded ad loaded");
            }

            @Override
            public void onRewardedVideoAdOpened() {
                showToast("Rewarded ad opened");
            }

            @Override
            public void onRewardedVideoStarted() {
                showToast("Rewarded ad started");
            }

            @Override
            public void onRewardedVideoAdClosed() {
                showToast("Rewarded ad closed");
//                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                showToast("Reward user with type: " + rewardItem.getType() + " amount: " + rewardItem.getAmount());
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                showToast("Rewarded ad left application");
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                hideProgressBar();
                showRewardedButton.setEnabled(true);
                showToast("Sample adapter rewarded video ad failed with code: " + errorCode);
            }
        });

        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        showRewardedButton.setEnabled(false);
        AdRequest request = getAdapterAdRequest(PersonalyRewarded.class);
        rewardedVideoAd.loadAd(adMobRewardedVideoAdUnitId, request);
    }
    // endregion

    // region AdMob Native
    @OnClick(R.id.admob_nat_show_button)
    public void onNativeShowClick() {
        moPubNativeAdUnitId = checkInputLayout(true, placementIdNatInputLayout, placementIdNatEditText);

    }

    // endregion

    private AdRequest getAdapterAdRequest(Class<? extends MediationAdapter> adapterClass) {
        Bundle bundle = new Bundle();
        bundle.putString(Field.CLICK_ID, getCustomParameter());
        AdRequest request = new AdRequest.Builder()
                .addNetworkExtrasBundle(adapterClass, bundle)
//                .addTestDevice("D2411AB56655212E1E595134C821CFCB")
                .build();
        return request;
    }

    private AdRequest getEventAdRequest(Class<? extends CustomEvent> eventClass) {
        Bundle bundle = new Bundle();
        bundle.putString(Field.CLICK_ID, getCustomParameter());
        AdRequest request = new AdRequest.Builder()
                .addCustomEventExtrasBundle(eventClass, bundle)
//                .addTestDevice("D2411AB56655212E1E595134C821CFCB")
                .build();
        return request;
    }

    // region example
    public void interstitialExample() {
        // Create request with PersonalyInterstitial.class
        AdRequest request = new AdRequest.Builder()
                .addCustomEventExtrasBundle(PersonalyInterstitial.class, null)
                .build();

        // Load the ad
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("AdUnitId");
        interstitialAd.loadAd(request);
    }

    private void rewardedExample() {
        // Create request with PersonalyRewarded.class
        AdRequest request = new AdRequest.Builder()
                .addNetworkExtrasBundle(PersonalyRewarded.class, null)
                .build();

        // Load the ad
        RewardedVideoAd rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.loadAd("AdMobAdUnitId", request);
    }
    // endregion
}
