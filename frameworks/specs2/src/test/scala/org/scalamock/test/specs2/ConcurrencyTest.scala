// Copyright (c) 2011-2015 ScalaMock Contributors (https://github.com/paulbutcher/ScalaMock/graphs/contributors)
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.scalamock.test.specs2

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.duration.SECONDS
import scala.language.reflectiveCalls

import org.scalamock.specs2.MockContext
import org.scalamock.test.mockable.TestTrait
import org.specs2.mutable.Specification

class ConcurrencyTest extends Specification {

  "Futures should work" in new MockContext {
    val s = stubFunction[Int]
    s.when().returns(1)
    Await.result(Future { s() }, Duration(10, SECONDS)) must be_==(1)
    s.verify().once()
  }

  "Concurrent mock access should work" in new MockContext {
    val m = mock[TestTrait]
    m.expects.oneParamMethod(42).repeated(500000).returning("a")

    val futures = (1 to 500000).map { _ =>
      Future { m.oneParamMethod(42) }
    }

    futures.foreach(future => Await.result(future, Duration(10, SECONDS)))
  }
}
