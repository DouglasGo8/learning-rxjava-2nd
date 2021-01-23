package com.packtpub.rxjava.basic.operators;

import com.google.common.collect.ImmutableList;
import io.reactivex.rxjava3.core.Observable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;

public class App {

    @Test
    public void ch03_01() {
        Observable.range(1, 20)
                .takeWhile(i -> i < 5)
                .subscribe(out::println);
    }

    @Test
    public void ch03_02() {

        Observable.just("Alpha", "Beta")
                .defaultIfEmpty("None")
                .subscribe(out::println);

        Observable.just("Alpha", "Beta")
                .filter(s -> s.startsWith("Z"))
                .switchIfEmpty(Observable.just("Sisters", "of", "Mercy"))
                .subscribe(out::println);
    }

    @Test
    public void ch03_05() {

        var obs = Observable.just("Alpha", "Beta", "Gamma");

        obs.filter(s -> s.length() != 5).subscribe(out::println);
        obs.take(2).subscribe(out::println);

    }

    @Test
    @SneakyThrows
    public void ch03_06() {

        final DateTimeFormatter f = DateTimeFormatter.ofPattern("ss:SSS");
        out.println(LocalDateTime.now().format(f));

        Observable.interval(300, TimeUnit.MILLISECONDS)
                .take(2, TimeUnit.SECONDS)
                .subscribe(i -> out.println(LocalDateTime.now().format(f) + " RECEIVED " + i));

        TimeUnit.SECONDS.sleep(2);
    }

    @Test
    public void ch03_07() {

        Observable.range(1, 40)
                .skip(20)
                .subscribe(out::println);
    }

    @Test
    public void ch03_10() {

        Observable.just("Beta", "Alpha", "Gamma")
                .distinct(String::length)
                .subscribe(out::println);

        Observable.just(1, 1, 2, 3, 3, 4, 5, 6, 6)
                .distinctUntilChanged()
                .subscribe(out::println);

        // elementAt get specific position of element
    }

    @Test
    public void ch03_14() {

        var dtf = DateTimeFormatter.ofPattern("M/d/yyyy");

        Observable.just("1/3/2016", "5/9/2016")
                .map(s -> LocalDate.parse(s, dtf))
                .subscribe(out::println);

        // cast
        Observable.just("Alpha", "Beta")
                .cast(Object.class)
                .subscribe(out::println);
        //
        Observable.just(Arrays.asList(new Foo(1, "field2"), new Foo(11, "field11")),
                Arrays.asList(new Bar(2L, "field3"), new Bar(21L, "field33")))
                //.map(Stream::of)
                .flatMap(p -> Observable.fromStream(p.stream()))
                .filter(p -> p instanceof Bar)
                .subscribe(out::println);


    }

    @Test
    public void ch3_15() {

        Observable.just("Coffee", "Tea")
                .startWithItem("COFFEE")
                .subscribe(out::println);

        Observable.just("Coffee", "Tea")
                .startWithArray("COFFEE", "-----------")
                .subscribe(out::println);

        Observable.just(1, 3, 44, 5, 11)
                //.sorted(Comparator.comparing(Integer::intValue))
                .sorted(Comparator.reverseOrder())
                //.sorted(Comparator.comparing(String::length))
                .subscribe(out::println);


    }

    @Test
    public void ch03_20() {

        // like reduce but with summing details
        Observable.just(1, 3, 44, 5, 11)
                .scan(Integer::sum)
                .subscribe(out::println);

        // like scan without summing details
        Observable.just(1, 3, 44, 5, 11)
                .reduce(Integer::sum)
                .subscribe(out::println);

        Observable.just("A", "B", "C")
                .count()
                .subscribe(out::print);
    }

    @Test
    public void ch03_25() {

        Observable.just(5, 3, 7, 11, 2, 14)
                // immediately operators
                // ========
                //.any()
                // ========
                // needs a filter ("Word", "Z") filter(s -> c.contains("Z") isEmpty will be false
                //.isEmpty()
                .all(i -> i < 10) // all item on the array are less than 10 (false)
                .subscribe(out::println);

        Observable.range(1, 20)
                .contains(3)
                .subscribe(out::println); // true


    }

    @Test
    public void ch03_29() {
        var obs1 = Observable.<String>just("A", "B", "C");
        var obs2 = Observable.<String>just("A", "B", "C");

        Observable.sequenceEqual(obs1, obs2).subscribe(out::println); // true
    }

    @Test
    public void ch03_30() {
        Observable.just("A", "B", "C")
                // Different array impl
                //.toList(CopyOnWriteArrayList::new)
                //.toSortedList() // ordered list
                .toMap(s -> s.charAt(0)) // {{A=A, B=B, C=C}
                // can use ConcurrentHashMap as 3rd parameter
                //.toMap(s-> s.charAt(0), String::length) // {{A=1, B=1, C=1}
                .subscribe(out::println);


        // [Gamma, Alpha, Beta]
        Observable.just("Alpha", "Beta", "Gamma", "Beta")
                .collect(HashSet<String>::new, HashSet::add)
                .subscribe(out::println);


        // Google Guava
        // [Alpha, Beta, Gamma]
        Observable.just("Alpha", "Beta", "Gamma")
                .collect(ImmutableList::builder, ImmutableList.Builder::add)
                .map(ImmutableList.Builder::build)
                .subscribe(out::println);
    }

    @Test
    public void ch03_43() {
        Observable.just(5, 2, 1, 0, 3)
                .map(i -> 10 / i)
                .subscribe(out::println, (err) -> out.println("FAIL, " + err.toString()));

        Observable.just(5, 2, 4, 0, 3)
                .map(i -> 10 / i)
                .onErrorReturnItem(-1)
                .subscribe(out::println, (err) -> out.println("FAIL, " + err.toString()));

        Observable.just(5, 2, 4, 0, 3)
                .map(i -> 10 / i)
                .onErrorReturn(e -> e instanceof ArithmeticException ? -1 : 0)
                .subscribe(out::println, (err) -> out.println("FAIL, " + err.toString()));

        Observable.just(5, 2, 4, 0, 3)
                .map(i -> 10 / i)
                .onErrorResumeWith(Observable.just(-1).repeat(3)) // or Observable.empty()
                .subscribe(out::println, (err) -> out.println("FAIL, " + err.toString()));

        Observable.just(5, 2, 4, 0, 3)
                .map(i -> 10 / i)
                .onErrorResumeNext((Throwable e) -> Observable.just(-1).repeat(3)) // or Observable.empty()
                .subscribe(out::println, (err) -> out.println("FAIL, " + err.toString()));


        // can use it with try catch inside .map


    }

    @Test
    public void ch03_48() {
        Observable.just(5, 2, 4, 0, 3)
                .map(i -> 10 / i)
                //.retry()// infinite loop
                .retry(1)
                .subscribe(out::println, err -> out.println("FAIL, " + err));
    }

    @Test
    public void ch03_50() {

        Observable.just("Hawk", "GTFO", "Assassins Creed")
                .doOnNext(out::println) // debug print item
                //.doAfterNext(out::println) // map first showed
                .doOnComplete(()-> out.println("done")) // after completed
                .map(String::length)
                .subscribe(out::println);

        Observable.just(5, 6, 2, 0, 1)
                .doOnError(e -> out.println("Source FAIL,"))
                .map(i -> 10 / i)
                .doOnError(e -> out.println("Division ERROR, "))
                .subscribe(out::println, err -> out.println("FAIL, " + err));

        // doOnEach
        // doOnSubscribe
        // doOnDispose
        // doOnSucess
        // doOnFinally

    }

    @Test
    public void replatePipes() {
        var field1 = "field|";
        var field2 = "field@mail.com|||";
        var field3 = "|field|@mail.com|";

        out.println(field1.replaceAll("\\|", ""));
        out.println(field2.replaceAll("\\|", ""));
        out.println(field3.replaceAll("\\|", ""));
    }

    @Test
    public void testList() {


        var set = new HashSet<>();

        set.add(Arrays.asList(new Foo(1, "field2"), new Foo(11, "field11")));
        set.add(Arrays.asList(new Bar(2L, "field3"), new Bar(21L, "field33")));

        //set
        //.stream()
        //.flatMap(p -> p)

        //.filter(c -> c instanceof Bar)
        set.forEach(out::println);


    }

    @Test
    @SneakyThrows
    public void testDelay() {
        Observable.just("Alpha", "Beta", "Gama")
                //.doOnNext(out::println)
                .delay(3, TimeUnit.SECONDS)
                .subscribe(out::println);

        TimeUnit.SECONDS.sleep(5);

        Observable.just("Alpha", "Beta", "Gama").repeat(2).subscribe(out::println);

        Observable.just("One").single("Four").subscribe(out::println);

        // returns Four
        Observable.just("One", "Two").filter(x -> x.contains("xxx")).single("Four").subscribe(out::println);
    }


    @Test
    @SneakyThrows
    public void timeInterval() {

        Observable.interval(2, TimeUnit.SECONDS)
                .doOnNext(out::println)
                .take(3)
                .timeInterval(TimeUnit.SECONDS)
                .subscribe(out::println);


        TimeUnit.SECONDS.sleep(7);
    }

    @Data
    @AllArgsConstructor
    class Foo {
        private int field1;
        private String field2;
    }

    @Data
    @AllArgsConstructor
    class Bar {
        private long field1;
        private String field2;
    }

}
