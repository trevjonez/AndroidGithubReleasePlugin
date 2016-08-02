/*
 * Kotlin adaptation of https://gist.github.com/cedricvidal/410b2c0131bae742959f
 */

package com.trevjonez.agrp.rx

import rx.Observable
import rx.functions.Func1
import java.util.Arrays.asList
import java.util.Collections.singleton

fun <T> cartesianProduct(obs1: Observable<T>, obs2: Observable<T>): Observable<Observable<T>> {
  return obs1.flatMap { outerIt -> obs2.map { Observable.from(asList(outerIt, it)) } }
}

fun <T> cartesianProduct(observables: Observable<Observable<T>>): Observable<Observable<T>> {
  val head = observables.take(1).flatMap { it.map(singletonF<T>()) }
  val tail = observables.skip(1)

  return Observable.merge(tail.reduce(head) { i1, i2 -> doCartesianProduct(i1, i2) })
}

fun <T> doCartesianProduct(t1: Observable<Observable<T>>, t2: Observable<T>): Observable<Observable<T>> {
  return t1.flatMap { outerIt -> t2.map { Observable.merge(outerIt, Observable.from(singleton(it))) } }
}

fun <T> singletonF(): Func1<T, Observable<T>> = Func1 { Observable.from(singleton(it)) }