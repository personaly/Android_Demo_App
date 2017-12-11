package ly.persona.sample.mopub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mopub.nativeads.MoPubNativeAdLoadedListener;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.PersonalyAdRenderer;
import com.mopub.nativeads.PersonalyExtras;
import com.mopub.nativeads.ViewBinder;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ly.persona.sample.BaseActivity;
import ly.persona.sample.R;
import ly.persona.sdk.model.CreativeType;
import ly.persona.sdk.model.NativeAdConfig;

import static ly.persona.sample.Keys.moPubNativeAdUnitId;

/**
 * Created by Oleg Tarashkevich on 09/11/2017.
 */

public class MoPubNativeRecyclerViewActivity extends BaseActivity {

    @BindView(R.id.mopub_nat_recycler_view) RecyclerView recyclerView;
    private MoPubRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ButterKnife.bind(this);

        setTitle("Persona.ly - MoPub native RecyclerView");

        setupNative();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyNative();
    }

    // region MoPub Native
    public void destroyNative() {
        // You must call this or the ad adapter may cause a memory leak.
        if (mRecyclerAdapter != null)
            mRecyclerAdapter.destroy();
    }

    public void setupNative() {

        MoPubNativeAdLoadedListener listener = new MoPubNativeAdLoadedListener() {
            @Override
            public void onAdLoaded(int position) {
                showToast("Native ad loaded: position " + position);
            }

            @Override
            public void onAdRemoved(int position) {
                showToast("Native ad failed: position " + position);
            }
        };

        final RecyclerView.Adapter originalAdapter = new DemoRecyclerAdapter();
        mRecyclerAdapter = new MoPubRecyclerAdapter(this, originalAdapter, new MoPubNativeAdPositioning.MoPubServerPositioning());
        mRecyclerAdapter.setAdLoadedListener(listener);

        // region Personaly
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

        final PersonalyAdRenderer personalyRenderer = new PersonalyAdRenderer(personalyBinder, config);

        mRecyclerAdapter.registerAdRenderer(personalyRenderer);
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

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerAdapter.loadAds(moPubNativeAdUnitId);
    }

    private static class DemoRecyclerAdapter extends RecyclerView.Adapter<DemoViewHolder> {
        private static final int ITEM_COUNT = 150;

        @Override
        public DemoViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new DemoViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final DemoViewHolder holder, final int position) {
            holder.textView.setText(String.format(Locale.US, "Content Item #%d", position));
        }

        @Override
        public long getItemId(final int position) {
            return (long) position;
        }

        @Override
        public int getItemCount() {
            return ITEM_COUNT;
        }
    }

    /**
     * A view holder for R.layout.simple_list_item_1
     */
    private static class DemoViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;

        public DemoViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
    // endregion
}
