package com.packtpub.rxjava.thinking.reactively;

import io.reactivex.rxjava3.core.Observable;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class App {


    @Test
    public void ch1_01() {

        Observable.just("COVID-19", "SARS", "EBOLA").forEach(out::println);
        out.println("-----");
        Observable.just("COVID-19", "SARS", "EBOLA").subscribe(out::println);
        out.println("-----");
        Observable.just("COVID-19", "SARS", "EBOLA").map(String::toLowerCase).subscribe(out::println);
        out.println("-----");
        Observable.just("COVID-19", "SARS", "EBOLA").map(String::length).subscribe(out::println);
    }

    @Test
    @SneakyThrows
    public void ch1_02() {

        Observable.interval(2, TimeUnit.SECONDS)
                .subscribe(out::println);

        TimeUnit.SECONDS.sleep(5);
    }

}
