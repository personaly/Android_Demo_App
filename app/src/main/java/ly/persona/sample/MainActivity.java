package ly.persona.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ly.persona.sample.admob.AdMobActivity;
import ly.persona.sample.mopub.MoPubActivity;
import ly.persona.sdk.AppWallAd;
import ly.persona.sdk.CampaignAd;
import ly.persona.sdk.NativeAd;
import ly.persona.sdk.NativeAdLayout;
import ly.persona.sdk.NativeAdView;
import ly.persona.sdk.OfferWallAd;
import ly.persona.sdk.Personaly;
import ly.persona.sdk.PopupOfferAd;
import ly.persona.sdk.listener.AdListener;
import ly.persona.sdk.listener.NativeAdListener;
import ly.persona.sdk.model.CanViewCallback;
import ly.persona.sdk.model.CreativeType;
import ly.persona.sdk.model.Field;
import ly.persona.sdk.model.Gender;
import ly.persona.sdk.model.Impression;
import ly.persona.sdk.model.NativeDataSet;
import ly.persona.sdk.model.PersonaError;

import static ly.persona.sample.Keys.APP_ID;
import static ly.persona.sample.Keys.appWallPlacementId;
import static ly.persona.sample.Keys.interstitialPlacementId;
import static ly.persona.sample.Keys.nativePlacementId;
import static ly.persona.sample.Keys.offersAppId;
import static ly.persona.sample.Keys.popupOfferPlacementId;
import static ly.persona.sample.Keys.rewardedPlacementId;

/**
 * Created by Oleg Tarashkevich on 10/11/2017.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    // region Fields
    private boolean autoCaching;
    private final String AUTO_CACHING_KEY = "autocaching";

    @BindView(R.id.auto_caching_checkbox) CheckBox checkBox;

    @BindView(R.id.app_id_input_layout) TextInputLayout appIdInputLayout;
    @BindView(R.id.user_id_input_layout) TextInputLayout userIdInputLayout;
    @BindView(R.id.app_id_edit_text) EditText appIdEditText;
    @BindView(R.id.user_id_edit_text) EditText userIdEditText;

    @BindView(R.id.placement_id_input_layout) TextInputLayout placementIdInputLayout;
    @BindView(R.id.placement_id_edit_text) EditText placementIdEditText;

    @BindView(R.id.placement_id_input_layout_2) TextInputLayout placementIdInputLayout2;
    @BindView(R.id.placement_id_edit_text_2) EditText placementIdEditText2;

    @BindView(R.id.offer_user_id_input_layout) TextInputLayout offerUserIdInputLayout;
    @BindView(R.id.offer_user_id_edit_text) EditText offerUserIdEditText;

    @BindView(R.id.native_id_input_layout) TextInputLayout nativeIdInputLayout;
    @BindView(R.id.native_edit_text) EditText nativeIdEditText;
    @BindView(R.id.native_caching_checkbox) CheckBox nativePreCacheCheckBox;
    @BindView(R.id.native_ad_container) LinearLayout nativeAdContainer;
    @BindView(R.id.native_video_checkbox) CheckBox videoAutoPlayCheckBox;
    @BindView(R.id.native_video_looping_checkbox) CheckBox videoLoopingCheckBox;
    @BindView(R.id.native_sound_checkbox) CheckBox soundAutoCheckBox;
    @BindView(R.id.native_count_edit_text) EditText nativeIdCountEditText;
    @BindView(R.id.native_recycler_view) RecyclerView nativeRecyclerView;

    @BindView(R.id.popup_offer_user_id_input_layout) TextInputLayout popupOfferUserIdInputLayout;
    @BindView(R.id.popup_offer_user_id_edit_text) EditText popupOfferUserIdEditText;

    @BindView(R.id.app_wall_input_layout) TextInputLayout appWallInputLayout;
    @BindView(R.id.app_wall_edit_text) EditText appWallEditText;

    private SharedPreferences sharedPreferences;
    private final NativeAdapter nativeAdapter = new NativeAdapter();
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        autoCaching = sharedPreferences.getBoolean(AUTO_CACHING_KEY, false);

        String userId = "123456789123456789";

        checkBox.setChecked(autoCaching);
        appIdEditText.setText(APP_ID);
        userIdEditText.setText(userId);
        placementIdEditText.setText(rewardedPlacementId);
        placementIdEditText2.setText(interstitialPlacementId);
        offerUserIdEditText.setText(offersAppId);
        appWallEditText.setText(appWallPlacementId);
        popupOfferUserIdEditText.setText(popupOfferPlacementId);
        nativeIdEditText.setText(nativePlacementId);

        // setup optional listeners for specific placementId
        CampaignAd.get(rewardedPlacementId).setListener(campaignAdLoadListener);
        CampaignAd.get(interstitialPlacementId).setListener(campaignAdLoadListener);
        PopupOfferAd.get(popupOfferPlacementId).setListener(adLoadListener);
        OfferWallAd.get(offersAppId).setListener(adOfferListener);
        AppWallAd.get(appWallPlacementId).setListener(adLoadListener);
        NativeAd.get(nativePlacementId).setListener(adNativeLoadListener);

        setTitle(getString(R.string.app_name) + " v." + BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE);

        nativeRecyclerView.setNestedScrollingEnabled(false);
        nativeRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
        nativeRecyclerView.setAdapter(nativeAdapter);

        Personaly.setAutoCaching(autoCaching);
//        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeAllAds();
    }

    private void init() {
        infoTextView.setText(null);
        // manually initCallback

        String appId = checkInputLayout(true, appIdInputLayout, appIdEditText);
        String userId = checkInputLayout(true, userIdInputLayout, userIdEditText);

        Personaly.CONFIG
                .setGender(Gender.MALE)
                .setDateOfBirth("1989/07/12")
//                .setDateOfBirth(1989, 8, 12)   // Optional method with numbers
                .setAge(19)
                .setUserId(userId);

        Personaly.setAutoCaching(autoCaching);

        Personaly.init(this, appId, initCallback);
        showProgressBar();
    }

    /**
     * Optional methods, disposing cached advertise
     */
    private void disposeAllAds() {
        CampaignAd.dispose(rewardedPlacementId);
        CampaignAd.dispose(interstitialPlacementId);
        PopupOfferAd.dispose(popupOfferPlacementId);
        OfferWallAd.dispose(offersAppId);
        AppWallAd.dispose(appWallPlacementId);
        NativeAd.dispose(nativePlacementId);
    }

    // region Campaign
    @OnClick({R.id.load_capaign_button,
            R.id.campaign_can_view_button,
            R.id.show_campaign_button,
            R.id.load_capaign_button_2,
            R.id.campaign_can_view_button_2,
            R.id.show_campaign_button_2
    })
    public void onCampaignClick(View v) {

        switch (v.getId()) {

            // First
            case R.id.load_capaign_button:
                rewardedPlacementId = checkInputLayout(true, placementIdInputLayout, placementIdEditText);
                CampaignAd.get(rewardedPlacementId)
                        .setListener(campaignAdLoadListener)
                        .setServerParams(getCustomParameters(getCustomParameter()))
                        .load();
                break;

            case R.id.campaign_can_view_button:
                rewardedPlacementId = placementIdEditText.getText().toString();
                CampaignAd.get(rewardedPlacementId)
                        .canView(new CanViewCallback() {
                            @Override
                            public void canView(boolean canView) {
                                showToast("Placement can be viewed: " + canView);
                            }
                        });
                break;

            case R.id.show_campaign_button:
                rewardedPlacementId = checkInputLayout(true, placementIdInputLayout, placementIdEditText);
                CampaignAd.get(rewardedPlacementId).show();
                break;

            // Second
            case R.id.load_capaign_button_2:
                interstitialPlacementId = checkInputLayout(true, placementIdInputLayout2, placementIdEditText2);
                CampaignAd.get(interstitialPlacementId)
                        .setListener(campaignAdLoadListener)
                        .setServerParams(getCustomParameters(getCustomParameter()))
                        .load();
                break;

            case R.id.campaign_can_view_button_2:
                interstitialPlacementId = placementIdEditText2.getText().toString();
                CampaignAd.get(interstitialPlacementId)
                        .canView(new CanViewCallback() {
                            @Override
                            public void canView(boolean canView) {
                                showToast("Placement can be viewed: " + canView);
                            }
                        });
                break;

            case R.id.show_campaign_button_2:
                interstitialPlacementId = checkInputLayout(true, placementIdInputLayout2, placementIdEditText2);
                CampaignAd.get(interstitialPlacementId).show();
                break;
        }
    }
    // endregion

    // region Other ad
    @OnClick({R.id.init_post_button,
            R.id.campaign_can_view_button,
            R.id.native_static_button,
            R.id.native_video_button,
            R.id.show_campaign_button,
            R.id.offers_ad_button,
            R.id.send_logs_button,
            R.id.popup_offers_show_button,
            R.id.popup_offers_ad_button,
            R.id.show_app_wall_button})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.init_post_button:
                init();
                break;

            case R.id.popup_offers_ad_button:
                popupOfferPlacementId = checkInputLayout(true, popupOfferUserIdInputLayout, popupOfferUserIdEditText);
                PopupOfferAd.get(popupOfferPlacementId)
                        .setListener(adLoadListener)
                        .setServerParams(getCustomParameters(getCustomParameter()))
                        .load();
                break;

            case R.id.popup_offers_show_button:
                PopupOfferAd.get(popupOfferPlacementId).show();
                break;

            case R.id.offers_ad_button:
                final Map<String, String> parameter = new HashMap<>();
                parameter.put(Field.PUB_CLICK_ID, getCustomParameter());

                offersAppId = checkInputLayout(true, offerUserIdInputLayout, offerUserIdEditText);
                OfferWallAd.get(offersAppId)
                        .setListener(adOfferListener)
                        .setServerParams(parameter)
                        .show();
                break;

            case R.id.show_app_wall_button:
                appWallPlacementId = checkInputLayout(true, appWallInputLayout, appWallEditText);
                AppWallAd.get(appWallPlacementId)
                        .setListener(adLoadListener)
                        .setServerParams(getCustomParameters(getCustomParameter()))
                        .show();
                break;

            case R.id.send_logs_button:
                sendEmailFile();
                break;

            case R.id.native_static_button:
                loadNativeAd(CreativeType.IMAGE);
                break;

            case R.id.native_video_button:
                loadNativeAd(CreativeType.VIDEO);
                break;
        }
    }

    @OnCheckedChanged(R.id.auto_caching_checkbox)
    public void onAutoCachingChanged(boolean isChecked) {
        autoCaching = isChecked;
        Personaly.setAutoCaching(autoCaching);
        if (sharedPreferences != null)
            sharedPreferences.edit().putBoolean(AUTO_CACHING_KEY, autoCaching).apply();
    }

    @OnClick(R.id.mopub_button)
    public void onMoPubClick() {
        Intent intent = new Intent(this, MoPubActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.admob_button)
    public void onAdMobClick() {
        Intent intent = new Intent(this, AdMobActivity.class);
        startActivity(intent);
    }
    // endregion

    // region InitCallback callback, optional feature
    private Personaly.InitCallback initCallback = new Personaly.InitCallback() {
        @Override
        public void onSuccess() {
            showToast("Init success");
            hideProgressBar();
        }

        @Override
        public void onFailure(Throwable throwable) {

            if (throwable instanceof PersonaError) {
                PersonaError error = (PersonaError) throwable;
                int errorCode = error.getErrorCode();
                // Do something with errorCode if it needed
            }

            showToast(throwable.getMessage());
            hideProgressBar();
        }
    };
    // endregion

    void loadNativeAd(@CreativeType String type) {
        nativePlacementId = nativeIdEditText.getText().toString();
        String number = nativeIdCountEditText.getText().toString();
        int count = Integer.parseInt(number);
        NativeAd.get(nativePlacementId)
                .setListener(adNativeLoadListener)
                .setPreCaching(nativePreCacheCheckBox.isChecked())
                .setCreativeType(type)
                .setServerParams(getCustomParameters(getCustomParameter()))
                .load(count);
    }

    private Map<String, String> getCustomParameters(String parameter) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(Field.CLICK_ID, parameter);
        return parameters;
    }

    // region Callbacks
    private NativeAdListener adNativeLoadListener = new NativeAdListener() {

        @Override
        public void onAdStartLoading() {
            showToast("onAdStartLoading");
            showProgressBar();
//            nativeAdContainer.removeAllViews();
        }

        @Override
        public void onNativeDataSetLoaded(List<NativeDataSet> dataSets) {
            showToast("Successfully loaded");
            hideProgressBar();

//            List<NativeDataSet> dataSets = NativeAd.get(nativePlacementId).getNativeDataSet();
//            NativeDataSet dataSet = dataSets.get(0);

            for (NativeDataSet dataSet : dataSets) {

                if (dataSet != null) {

                    //  region Create and populate one view
                    // Create custom view
//                    NativeAdView adLayout = new NativeAdView.Builder(MainActivity.this, R.layout.pn_ad_view_layout)
//                            .titleId(R.id.pn_title)
//                            .descriptionId(R.id.pn_description)
//                            .appDeveloperId(R.id.pn_app_developer)
//                            .iconId(R.id.pn_icon)
//                            .mediaViewId(R.id.pn_media_view)
//                            .callToActionId(R.id.pn_button)
//                            .ratingBarId(R.id.pn_rating_bar)
//                            .reviewsId(R.id.pn_reviews)
//                            .privacyPolicyId(R.id.pn_privacy_policy)
//                            .build();


//                    // For documentation
//                    NativeAdView adLayout = new NativeAdView.Builder(context, R.layout.pn_ad_view_layout)
//                            .titleId(R.id.pn_title)                 // TextView resource id
//                            .descriptionId(R.id.pn_description)     // TextView resource id
//                            .appDeveloperId(R.id.pn_app_developer)  // TextView resource id
//                            .iconId(R.id.pn_icon)                   // ImageView resource id
//                            .mediaView(R.id.pn_media_view)          // MediaView resource id
//                            .buttonId(R.id.pn_button)               // Button resource id
//                            .ratingBarId(R.id.pn_rating_bar)        // RatingBar resource id
//                            .reviewsId(R.id.pn_reviews)             // TextView resource id
//                            .config(NativeAdConfig.create())        // Optional configuration
//                            .privacyPolicyId(R.id.pn_privacy_policy)
//                            .build();


                    // Optional setup of NativeAdConfig
//                    adLayout.getNativeAdConfig()
//                            .setCreativeType(CreativeType.ALL)
//                            .setVideoMuted(soundAutoCheckBox.isChecked())
//                            .setVideoLooping(videoLoopingCheckBox.isChecked())
//                            .setVideoAutoPlay(videoAutoPlayCheckBox.isChecked());

                    // Populate NativeAdView with data set
//                    NativeAd.populateNativeAdLayout(adLayout, dataSet);
                    // endregion

                    nativeAdapter.setDataSet(dataSets);
                }
            }
        }

        @Override
        public void onNativeAdLayoutPopulated(NativeAdLayout adLayout) {
//            nativeAdContainer.addView((View) adLayout);
            showToast("View is populated and showed");
        }

        @Override
        public void onAdFailed(Throwable throwable) {

            if (throwable instanceof PersonaError) {
                PersonaError error = (PersonaError) throwable;
                int errorCode = error.getErrorCode();
                // Do something with errorCode if it needed
            }

            showToast(throwable.getMessage());
            hideProgressBar();
        }

        @Override
        public void onAdShowed() {
            showToast("onAdShowed");
        }

        @Override
        public void onAdClicked() {
            showToast("onAdClicked");
        }

        @Override
        public void onAdClosed() {
            showToast("onAdClosed");
        }

    };

    private AdListener campaignAdLoadListener = new Listener() {

        @Override
        public void onAdStartLoading() {
            super.onAdStartLoading();
            showProgressBar();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            hideProgressBar();
        }

        @Override
        public void onAdShowed() {
            super.onAdShowed();

            // TODO Perform manual caching
//            CampaignAd.get(interstitialPlacementId).load();
        }

        @Override
        public void onAdFailed(Throwable throwable) {
            super.onAdFailed(throwable);
            hideProgressBar();
        }
    };

    private AdListener adLoadListener = new Listener() {

        @Override
        public void onAdStartLoading() {
            super.onAdStartLoading();
            showProgressBar();
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            hideProgressBar();
        }

        @Override
        public void onAdFailed(Throwable throwable) {
            super.onAdFailed(throwable);
            hideProgressBar();
        }
    };

    private AdListener adOfferListener = new Listener();

    private class Listener extends AdListener {

        @Override
        public void onAdStartLoading() {
            showToast("onAdStartLoading");
        }

        @Override
        public void onAdLoaded() {
            showToast("Successfully loaded");
        }

        @Override
        public void onAdFailed(Throwable throwable) {

            if (throwable instanceof PersonaError) {
                PersonaError error = (PersonaError) throwable;
                int errorCode = error.getErrorCode();
                // Do something with errorCode if it needed
            }

            showToast(throwable.getMessage());
        }

        @Override
        public void onAdRewarded(Impression impression) {
            String details = impression != null ? impression.toString() : null;
            showToast("onAdRewarded: " + details);
        }

        @Override
        public void onAdClosed() {
            showToast("onAdClosed");
        }

        @Override
        public void onAdShowed() {
            showToast("onAdShowed");
        }

        @Override
        public void onAdClicked() {
            showToast("onAdClicked");
        }
    }
    // endregion
}
