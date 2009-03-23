/*
 * Copyright (c) 2008-2009 by Xuggle Inc. All rights reserved.
 *
 * It is REQUESTED BUT NOT REQUIRED if you use this library, that you let 
 * us know by sending e-mail to info@xuggle.com telling us briefly how you're
 * using the library and what you like or don't like about it.
 *
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
// for std::numeric_limits
#include <limits>

#include <com/xuggle/xuggler/Rational.h>

namespace com { namespace xuggle { namespace xuggler
{

  Rational :: Rational()
  {
    // default to 0 for the value.
    mRational.den = 1;
    mRational.num = 0;
  }

  Rational :: ~Rational()
  {
  }

  Rational *
  Rational :: make(double d)
  {
    Rational *result=0;
    result = Rational::make();
    if (result) {
      result->mRational = av_d2q(d, 0x7fffffff);
    }
    return result;
  }

  Rational *
  Rational :: make(AVRational *src)
  {
    Rational *result=0;
    if (src)
    {
      result = Rational::make();
      if (result) {
        result->mRational = *src;
      }
    }
    return result;
  }

  IRational *
  Rational :: copy()
  {
    return Rational::make(this);
  }

  Rational *
  Rational :: make(Rational *src)
  {
    Rational *result=0;
    if (src)
    {
      result = Rational::make();
      if (result) {
        result->mRational = src->mRational;
      }
    }
    return result;
  }
  Rational *
  Rational :: make(int32_t num, int32_t den)
  {
    Rational *result=0;
    result = Rational::make();
    if (result) {
      result->mRational.num = num;
      result->mRational.den = den;
      (void) result->reduce(num, den, FFMAX(den, num));
    }
    return result;
  }
  int32_t
  Rational :: compareTo(IRational *other)
  {
    int32_t result = 0;
    Rational *arg=dynamic_cast<Rational*>(other);
    if (arg)
      result = av_cmp_q(mRational, arg->mRational);
    return result;
  }

  double
  Rational :: getDouble()
  {
    double result = 0;
    // On some runs in Linux calling av_q2d will raise
    // a FPE instead of returning back NaN or infinity,
    // so we try to short-circuit that here.
    if (mRational.den == 0)
      if (mRational.num == 0)
        result = std::numeric_limits<double>::quiet_NaN();
      else
        result = std::numeric_limits<double>::infinity();
    else
      result =  av_q2d(mRational);
    return result;
  }

  int32_t
  Rational :: reduce(int64_t num, int64_t den, int64_t max)
  {
    int32_t result = 0;
    result =  av_reduce(&mRational.num, &mRational.den,
        num, den, max);
    return result;
  }

  IRational *
  Rational :: multiply(IRational *other)
  {
    Rational *result = 0;
    Rational *arg=dynamic_cast<Rational*>(other);
    if (arg)
    {
      result = Rational::make();
      if (result)
      {
        result->mRational = av_mul_q(this->mRational,
            arg->mRational);
      }
    }
    return result;
  }

  IRational *
  Rational :: divide(IRational *other)
  {
    Rational *result = 0;
    Rational *arg=dynamic_cast<Rational*>(other);
    if (arg)
    {
      result = Rational::make();
      if (result)
      {
        result->mRational = av_div_q(this->mRational,
            arg->mRational);
      }
    }
    return result;
  }

  IRational *
  Rational :: subtract(IRational *other)
  {
    Rational *result = 0;
    Rational *arg=dynamic_cast<Rational*>(other);
    if (arg)
    {
      result = Rational::make();
      if (result)
      {
        result->mRational = av_sub_q(this->mRational,
            arg->mRational);
      }
    }
    return result;
  }
  IRational *
  Rational :: add(IRational *other)
  {
    Rational *result = 0;
    Rational *arg=dynamic_cast<Rational*>(other);
    if (arg)
    {
      result = Rational::make();
      if (result)
      {
        result->mRational = av_add_q(this->mRational,
            arg->mRational);
      }
    }
    return result;
  }

  int64_t
  Rational :: rescale(int64_t origValue, IRational *origBase)
  {
    int64_t retval=origValue;
    Rational *arg=dynamic_cast<Rational*>(origBase);

    if (arg)
    {
      retval = av_rescale_q(origValue, arg->mRational, this->mRational);
    }
    return retval;
  }
}}}