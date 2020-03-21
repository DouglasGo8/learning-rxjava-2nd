package com.packtpub.rxjava.observable.observer;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.List;
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

    // stop 56 pg

}
