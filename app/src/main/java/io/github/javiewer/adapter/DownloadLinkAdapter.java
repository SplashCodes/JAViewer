package io.github.javiewer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.DownloadLink;
import io.github.javiewer.adapter.item.MagnetLink;
import io.github.javiewer.network.provider.DownloadLinkProvider;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project: JAViewer
 */
public class DownloadLinkAdapter extends ItemAdapter<DownloadLink, DownloadLinkAdapter.ViewHolder> {

    private Activity mParentActivity;

    private DownloadLinkProvider provider;

    public DownloadLinkAdapter(List<DownloadLink> links, Activity mParentActivity, DownloadLinkProvider provider) {
        super(links);
        this.mParentActivity = mParentActivity;
        this.provider = provider;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_download, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DownloadLink link = getItems().get(position);

        holder.parse(link);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!link.hasMagnetLink()) {
                    final ProgressDialog mDialog;
                    mDialog = new ProgressDialog(mParentActivity);
                    mDialog.setTitle("请稍后");
                    mDialog.setMessage("正在获取磁力链接");
                    mDialog.setIndeterminate(false);
                    mDialog.setCancelable(false);
                    mDialog.show();

                    Call<ResponseBody> call = provider.get(link.getLink());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                MagnetLink magnetLink = provider.parseMagnetLink(response.body().string());
                                onMagnetGet(magnetLink.getMagnetLink());
                            } catch (Throwable e) {
                                onFailure(call, e);
                            }

                            mDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } else {
                    onMagnetGet(link.getMagnetLink());
                }
            }
        });
    }

    public void onMagnetGet(final String magnetLink) {
        if (!magnetLink.isEmpty()) {
            AlertDialog mDialog = new AlertDialog.Builder(mParentActivity)
                    .setTitle("磁力链接")
                    .setMessage(magnetLink)
                    .setNeutralButton("复制到剪贴板", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clip = (ClipboardManager) mParentActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                            clip.setPrimaryClip(ClipData.newPlainText("magnet-link", magnetLink));
                            Toast.makeText(mParentActivity, "磁力链接：" + magnetLink + " 已复制到剪贴板", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(magnetLink));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mParentActivity.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

        } else {
            Toast.makeText(mParentActivity, "磁力链接获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.download_title)
        public TextView mTextTitle;

        @BindView(R.id.download_size)
        public TextView mTextSize;

        @BindView(R.id.download_date)
        public TextView mTextDate;

        @BindView(R.id.layout_download)
        public View mView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void parse(DownloadLink link) {
            mTextSize.setText(link.getSize());
            mTextTitle.setText(link.getTitle());
            mTextDate.setText(link.getDate());
        }
    }
}
