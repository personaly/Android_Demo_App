package ly.persona.sample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ly.persona.sdk.NativeAd;
import ly.persona.sdk.NativeAdView;
import ly.persona.sdk.model.NativeDataSet;

/**
 * Created by Oleg Tarashkevich on 08/11/2017.
 */

public class NativeAdapter extends RecyclerView.Adapter<NativeAdapter.NativeViewHolder> {

    private List<NativeDataSet> dataSet = new ArrayList<>();

    @Override
    public NativeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_native, parent, false);
        return new NativeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NativeViewHolder holder, int position) {
        NativeDataSet data = getNativeDataSet(position);
        NativeAd.populateNativeAdLayout(holder.nativeAdView, data);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public NativeDataSet getNativeDataSet(int position) {
        return dataSet.get(position);
    }

    public void setDataSet(List<NativeDataSet> nativeDataSets) {
        dataSet = nativeDataSets;
        notifyDataSetChanged();
    }

    public static class NativeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.native_ad_view) NativeAdViewSmall nativeAdView;

        public NativeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
