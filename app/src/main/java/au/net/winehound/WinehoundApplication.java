package au.net.winehound;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import org.androidannotations.annotations.EApplication;

@EApplication
public class WinehoundApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup our tracker
        GoogleAnalytics.getInstance(this).enableAutoActivityReports(this);
        final Tracker myTracker = GoogleAnalytics.getInstance(this).newTracker(R.xml.global_tracker);

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                myTracker,                                        // Currently used Tracker.
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        myTracker.send(new HitBuilders.ExceptionBuilder()
                                        .setDescription("Custom exception handler\n"+
                                                new StandardExceptionParser(WinehoundApplication.this, null)
                                                        .getDescription(Thread.currentThread().getName(), throwable))
                                        .setFatal(true)
                                        .build());

                        defaultHandler.uncaughtException(thread, throwable);
                    }
                },

//                Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                this);                                         // Context of the application.

        // Make myHandler the new default uncaught exception handler.
        Thread.setDefaultUncaughtExceptionHandler(myHandler);
    }
}
