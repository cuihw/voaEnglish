package example.com.zztest.download;

import java.util.ArrayList;
import java.util.List;

public class DownloadManager {

    private List<Task> tasks = new ArrayList<>();

    static DownloadManager instances = new DownloadManager();

    private Boolean isIdle = true;

    private DownloadManager(){}

    private List<IManagerOnDownload> managerOnDownloads = new ArrayList<>();
    private Task currentTask;

    public void registerListener(IManagerOnDownload managerOnDownload) {
        managerOnDownloads.add(managerOnDownload);
    }

    public static DownloadManager getInstances() {
        return instances;
    }

    public void unRegisterListener(IManagerOnDownload managerOnDownload) {
        managerOnDownloads.remove(managerOnDownload);
    }

    public void addTask(Task task){
        if (!tasks.contains(task)) tasks.add(task);
        startDownload();
    }

    private synchronized void startDownload() {
        if (isIdle) {
            if (tasks.size() > 0) {
                currentTask = tasks.get(0);
                currentTask.setOnDownload(new IOnDownload() {
                    @Override
                    public void onFinished(String status) {
                        isIdle = true;
                        tasks.remove(currentTask);

                        startDownload();
                    }
                });
                currentTask.startDownload();
                isIdle = false;
            }
        }
    }


}
