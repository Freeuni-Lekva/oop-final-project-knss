package com.example.submissions.Utils.SubmissionsBus;

import com.example.submissions.Response.SubmissionResponse;
import com.google.gson.Gson;
import javax.servlet.AsyncContext;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllSubmissionsEventBus {

    private static final AllSubmissionsEventBus INSTANCE = new AllSubmissionsEventBus();

    private final ExecutorService writerPool = Executors.newSingleThreadExecutor();

    private final List<AsyncContext> listeners = new CopyOnWriteArrayList<>();

    public AllSubmissionsEventBus() {

    }

    public static AllSubmissionsEventBus get() {
        return INSTANCE;
    }

    public void register(AsyncContext asyncContext) {
        listeners.add(asyncContext);
    }

    public void unregister(AsyncContext asyncContext) {
        listeners.remove(asyncContext);
    }

    public void publish(String eventName, SubmissionResponse submissionResponse) {
        String payload =
                "event: " + eventName + "\n" +
                        "data: " + new Gson().toJson(submissionResponse) + "\n\n";

        writerPool.submit(() -> {
            List<AsyncContext> toRemove = new ArrayList<>();
            for (AsyncContext asyncContext : listeners) {
                try {
                    PrintWriter w = asyncContext.getResponse().getWriter();
                    w.write(payload);
                    w.flush();
                } catch (IOException e) {
                    asyncContext.complete();
                    toRemove.add(asyncContext);
                }
            }
            listeners.removeAll(toRemove);
        });
    }

}
