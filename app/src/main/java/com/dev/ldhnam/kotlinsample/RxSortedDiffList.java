package com.dev.ldhnam.kotlinsample;

import com.dev.ldhnam.kotlinsample.util.Event;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxSortedDiffList<T> {

    private static Executor sExecutor = Executors.newSingleThreadExecutor();

    private SnappySortedList<T> mSnappySortedList;

    public RxSortedDiffList(Class<T> clazz, RxSortedListCallback<T> callback) {
        mSnappySortedList = new SnappySortedList<>(clazz, callback);
    }

    public RxSortedDiffList(Class<T> clazz, RxSortedListCallback<T> callback, int initialCapacity) {
        mSnappySortedList = new SnappySortedList<>(clazz, callback, initialCapacity);
    }

    public Observable<Integer> add(T item) {
        return Observable.fromCallable(() -> mSnappySortedList.add(item)).compose(onBackground());
    }

    public Observable<Object> addAll(List<T> users) {
        return Observable.fromCallable(() -> {
            mSnappySortedList.addAll(users);
            return Event.INSTANCE;
        }).compose(onBackground());
    }

    public Observable<Object> clear() {
        return Observable.fromCallable(() -> {
            mSnappySortedList.clear();
            return Event.INSTANCE;
        }).compose(onBackground());
    }

    public T get(int index) {
        return mSnappySortedList.get(index);
    }

    public List<T> getData() {
        return Arrays.asList(mSnappySortedList.mData);
    }

    public Observable<Boolean> remove(T item) {
        return Observable.fromCallable(() -> mSnappySortedList.remove(item)).compose(onBackground());
    }

    public Observable<T> removeItemAt(int index) {
        return Observable.fromCallable(() -> mSnappySortedList.removeItemAt(index)).compose(onBackground());
    }

    public Observable<Object> set(Collection<T> items) {
        return mSnappySortedList.set(items)
                .subscribeOn(Schedulers.from(sExecutor));
    }

    public Observable<Object> set(Collection<T> items, boolean isSorted) {
        return mSnappySortedList.set(items, isSorted)
                .subscribeOn(Schedulers.from(sExecutor));
    }

    public int size() {
        return mSnappySortedList.size();
    }

    public Observable<Object> updateItemAt(int index, T item) {
        return Observable.fromCallable(() -> {
            mSnappySortedList.updateItemAt(index, item);
            return Event.INSTANCE;
        }).compose(onBackground());
    }

    private <T2> ObservableTransformer<T2, T2> onBackground() {
        return observable -> Observable.just(Event.INSTANCE)
                .subscribeOn(Schedulers.from(sExecutor))
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(o -> observable);
    }
}