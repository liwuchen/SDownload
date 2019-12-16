package com.lwc.download;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @Package: com.lwc.download
 * @ClassName: DownloadProgressListener
 * @Description: 下载进度处理
 * @Author: liwuchen
 * @CreateDate: 2019/10/15
 */
public class DownloadProgressListener {

    private DownloadInfo info;
    private DownloadListener listener;

    public DownloadProgressListener(DownloadInfo downloadInfo, DownloadListener listener) {
        this.info = downloadInfo;
        this.listener = listener;
    }
    /**
     * @param read 已下载长度
     * @param contentLength 总长度
     * @param done 是否下载完毕
     */
    public void progress(long read, long contentLength, final boolean done){
        //更新已下载的文件大小
        if (info.getContentLength() > contentLength) {
            read = read + (info.getContentLength() - contentLength);
        } else {
            info.setContentLength(contentLength);
        }
        info.setReadLength(read);
        //切换到主线程
        Disposable d = Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        if (done) {
                            listener.onFinishDownload();
                            info.setState(DownState.FINISH);
                        } else {
                            if (info.getContentLength() > 0) {
                                listener.onProgress((int) (info.getReadLength() * 100 / info.getContentLength()));
                            }
                        }
                    }
                });
    }

}
