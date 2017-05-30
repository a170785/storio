package com.pushtorefresh.storio.contentresolver.operations.internal;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.operations.internal.CompletableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.operations.internal.FlowableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.operations.internal.SingleOnSubscribeExecuteAsBlocking;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJava2IsNotAvailable;

public class RxJavaUtils {

    private RxJavaUtils() {
        throw new IllegalStateException("No instances please.");
    }

    @CheckResult
    @NonNull
    public static <T> Flowable<T> createFlowable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T> operation,
            @NonNull BackpressureStrategy backpressureStrategy
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxObservable()");

        final Flowable<T> flowable = Flowable.create(
                new FlowableOnSubscribeExecuteAsBlocking<T>(operation), backpressureStrategy
        );

        return subscribeOn(
                storIOContentResolver,
                flowable
        );
    }


    @CheckResult
    @NonNull
    public static <T> Single<T> createSingle(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxSingle()");

        final Single<T> single = Single.create(new SingleOnSubscribeExecuteAsBlocking<T>(operation));

        return subscribeOn(
                storIOContentResolver,
                single
        );
    }

    @CheckResult
    @NonNull
    public static <T> Completable createCompletable(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull PreparedOperation<T> operation
    ) {
        throwExceptionIfRxJava2IsNotAvailable("asRxCompletable()");

        final Completable completable = Completable.create(new CompletableOnSubscribeExecuteAsBlocking(operation));

        return subscribeOn(
                storIOContentResolver,
                completable
        );
    }

    @CheckResult
    @NonNull
    public static <T> Flowable<T> subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Flowable<T> flowable
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultRxScheduler();
        return scheduler != null ? flowable.subscribeOn(scheduler) : flowable;
    }

    @CheckResult
    @NonNull
    public static <T> Single<T> subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Single<T> single
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultRxScheduler();
        return scheduler != null ? single.subscribeOn(scheduler) : single;
    }

    @CheckResult
    @NonNull
    public static Completable subscribeOn(
            @NonNull StorIOContentResolver storIOContentResolver,
            @NonNull Completable completable
    ) {
        final Scheduler scheduler = storIOContentResolver.defaultRxScheduler();
        return scheduler != null ? completable.subscribeOn(scheduler) : completable;
    }
}
