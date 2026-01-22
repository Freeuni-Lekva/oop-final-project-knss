package com.example.submissions.servlets;

import com.example.submissions.Utils.SubmissionsBus.AllSubmissionsEventBus;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AllSubmissionsListAsyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Connection","keep-alive");
        response.setHeader("Access-Control-Allow-Origin", "*");

        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(0);

        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) {
                AllSubmissionsEventBus.get().unregister(asyncContext);
            }
            @Override
            public void onTimeout(AsyncEvent event) {
                AllSubmissionsEventBus.get().unregister(asyncContext);
                asyncContext.complete();
            }
            @Override
            public void onError(AsyncEvent event) {
                AllSubmissionsEventBus.get().unregister(asyncContext);
                asyncContext.complete();
            }
            @Override
            public void onStartAsync(AsyncEvent event) {
            }
        });

        AllSubmissionsEventBus.get().register(asyncContext);
    }

}
