package ly.persona.example.mopub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.PersonalyAdRenderer;
import com.mopub.nativeads.PersonalyExtras;
import com.mopub.nativeads.ViewBinder;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import ly.persona.example.BaseActivity;
import ly.persona.example.R;
import ly.persona.sdk.NativeAdView;
import ly.persona.sdk.model.CreativeType;
import ly.persona.sdk.model.Field;
import ly.persona.sdk.model.NativeAdConfig;

import static ly.persona.example.Keys.moPubNativeAdUnitId;

/**
 * Created by Oleg Tarashkevich on 09/11/2017.
 */

public class MoPubNativeViewActivity extends BaseActivity {

    @BindView(R.id.native_ad_view) NativeAdView nativeAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);
        ButterKnife.bind(this);

        setTitle("Persona.ly - MoPub native view");

        setupNative();
    }

    // region MoPub Native
    public void setupNative() {
        nativeAdView.setVisibility(View.GONE);

        showProgressBar();
        MoPubNative.MoPubNativeNetworkListener moPubNativeListener = new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(NativeAd nativeAd) {
                hideProgressBar();
                nativeAdView.setVisibility(View.VISIBLE);
                showToast("Native ad loaded");
                nativeAd.setMoPubNativeEventListener(new NativeAd.MoPubNativeEventListener() {
                    @Override
                    public void onImpression(View view) {

                    }

                    @Override
                    public void onClick(View view) {

                    }
                });
                nativeAd.renderAdView(nativeAdView);
                nativeAd.prepare(nativeAdView);
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                hideProgressBar();
                showToast("Native ad failed: " + errorCode.toString());
            }
        };

        MoPubNative moPubNative = new MoPubNative(this, moPubNativeAdUnitId, moPubNativeListener);

        String clickId = null;
        Intent intent = getIntent();
        if (intent != null){
           clickId = intent.getStringExtra(Field.CLICK_ID);
        }
        // TODO Server Params
        final Map<String, Object> params = new HashMap<>();
        params.put(Field.CLICK_ID, clickId);
        moPubNative.setLocalExtras(params);

        final PersonalyAdRenderer personalyRenderer = getRenderer();
        moPubNative.registerAdRenderer(personalyRenderer);
        moPubNative.makeRequest();
        // endregion

        // region Mopub views
//        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(
//                new ViewBinder.Builder(R.layout.native_ad_list_item)
//                        .titleId(R.id.native_title)
//                        .textId(R.id.native_text)
//                        .mainImageId(R.id.native_main_image)
//                        .iconImageId(R.id.native_icon_image)
//                        .callToActionId(R.id.native_cta)
//                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
//                        .build()
//        );
//
//        // Set up a renderer for a video native ad.
//        MoPubVideoNativeAdRenderer moPubVideoNativeAdRenderer = new MoPubVideoNativeAdRenderer(
//                new MediaViewBinder.Builder(R.layout.video_ad_list_item)
//                        .titleId(R.id.native_title)
//                        .textId(R.id.native_text)
//                        .mediaLayoutId(R.id.native_media_layout)
//                        .iconImageId(R.id.native_icon_image)
//                        .callToActionId(R.id.native_cta)
//                        .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
//                        .build());
//
//        mRecyclerAdapter.registerAdRenderer(moPubStaticNativeAdRenderer);
//        mRecyclerAdapter.registerAdRenderer(moPubVideoNativeAdRenderer);
        // endregion
    }

    private PersonalyAdRenderer getRenderer(){
        // Setup view ids for Personaly
        PersonalyExtras extras = PersonalyExtras.create()
                .mediaView(R.id.pn_media_view)
                .appDeveloperId(R.id.pn_app_developer)
                .ratingBarId(R.id.pn_rating_bar)
                .privacyPolicyId(R.id.pn_privacy_policy)
                .reviewsId(R.id.pn_reviews);

        // Setup view ids for MoPub
        ViewBinder personalyBinder = new ViewBinder.Builder(R.layout.pn_ad_view_layout)
                .titleId(R.id.pn_title)
                .textId(R.id.pn_description)
                .iconImageId(R.id.pn_icon)
                .callToActionId(R.id.pn_button)
                .addExtras(extras)
                .build();

        // Optional configuration for NativeAdView
        NativeAdConfig config = NativeAdConfig.create()
                .setCreativeType(CreativeType.ALL)
                .setVideoLooping(true)
                .setVideoMuted(false);

        return new PersonalyAdRenderer(personalyBinder, config);
    }
    // endregion
}
