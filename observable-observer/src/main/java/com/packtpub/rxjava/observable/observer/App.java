package com.packtpub.rxjava.observable.observer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.lang.System.out;

public class App {


    /**
     * Observable class
     * Observer interface
     */


    @Test
    public void ch02_01() {

        Observable.create(e -> {
            // onNext
            IntStream.rangeClosed(1, 4).forEach(e::onNext);
            // onComplete
            e.onComplete();
        }).subscribe(out::println, Throwable::printStackTrace);

        Observable.<String>create(e -> {
            try {
                e.onNext("Wait");
                e.onNext("Girl");
                e.onComplete();
            } catch (Throwable err) {
                e.onError(err);
            }
        }).map(String::length).filter(e -> e > 5).subscribe(out::println, Throwable::printStackTrace);
    }

    @Test
    public void ch02_05() {
        Observable.just("Boris", "Brejcha", "Album", "22")
                .map(String::length)
                .filter(i -> i > 5)
                .subscribe(out::println);
    }

    // List
    @Test
    public void ch02_06() {

        Observable.fromIterable(List.of("Moon", "Dancer", "GTFO"))
                .map(String::length)
                .filter(i -> i > 5)
                .subscribe(out::println);
    }

    @Test
    public void ch02_07() {

        var observer = Observable.fromIterable(List.of("Moon", "Dancer", "GTFO", "blablabla"));

        // Consumer<Integer>
        // Consumer<Throwable>
        // Action ()->

        observer.map(String::length)
                .filter(i -> i > 5)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // disposing, disregard actions
                    }

                    @Override
                    public void onNext(@NonNull Integer n) {
                        out.println(n);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        out.println("Done !");
                    }
                });

    }

    // cold vs hot (cold)
    @Test
    public void ch02_11() {
        var source = Observable.just("Alpha", "Beta", "Gamma");
        source.subscribe(out::println);
        source.map(String::length).filter(i -> i >= 5).subscribe(out::println);
    }

    // cold vs hot (hot)
    @Test
    @SneakyThrows
    public void ch02_14() {
        var source = Observable.just("Alpha", "Beta", "Gamma").publish();

        source.subscribe(out::println);
        source.map(String::length).filter(i -> i >= 5).subscribe(out::println);

        TimeUnit.SECONDS.sleep(3);
        source.connect();
    }

    @Test
    public void ch02_15() {
        Observable.range(1, 3).subscribe(out::println);
        Observable.range(5, 3).subscribe(out::println);
    }

    @Test
    public void ch02_16() {
        Future<String> v = Executors.newSingleThreadExecutor().submit(() -> "Tears on darkness");
        Observable.fromFuture(v).subscribe(out::println);
    }

    // Observable.empty() -> with onCompleted method
    // Observable.never() -> without onComplete method
    // Observable.error()

    @Test
    public void ch02_24() {
        // static int count = 5;
        var source1 = Observable.defer(() -> Observable.range(1, 3)); // in a stateful context defer the state
        // source will be to 5
        // count = 10
        // source will changed to 10

    }

    @Test
    public void ch2_25() {
        Observable.fromCallable(() -> 1 / 0).subscribe(out::println, out::println); // err.toString
    }

    @Test
    public void ch02_26() {
        Single.just("1").subscribe(out::println); // can be just one parameter
        Maybe.just(100).subscribe(out::println); // prints 100
        Completable.fromRunnable(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).subscribe(() -> out.println("Done"));
    }

    // Disposable to stop Emissions

    @Test
    public void ch02_36() {

        var source = Observable.create(emitter -> {
            try {

                for (int i = 0; i < 1000; i++) {
                    while (!emitter.isDisposed()) {
                        emitter.onNext(i);
                    }
                }

                if (emitter.isDisposed()) {
                    return;
                }

                emitter.onComplete();


            } catch (Throwable e) {
                emitter.onError(e);
            }
        });
    }
}
