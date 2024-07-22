package com.chcknbyz.mio.models

import scala.util.Random

import cats.implicits._
import cats.parse.Parser
import cats.parse.Rfc5234.digit

// TODO: by should be Dice so we can do things like 2d10 + 4d8 + 10
trait Exp
case class Dice(count: Int, size: Int) extends Exp
case class Number(value: Int) extends Exp
case class Op(op: Ops, left: Exp, right: Exp) extends Exp

enum Ops:
  case Add, Mul, Div, Sub

object Dice {
  // <number>d<size>(<op><number>)
  val number = digit.rep.string.map(_.toInt).map(Number.apply) // 55
  val digits = digit.rep.string.map(_.toInt)
  // 1d20
  val diceP = (digits ~ Parser.char('d').void ~ digits).map { case ((c, _), s) => Dice(c, s) }
  // +2 | +2d10
  val modiferP = Parser
    .oneOf(
      Parser.char('+').as(Ops.Add) ::
        Parser.char('-').as(Ops.Sub) ::
        Parser.char('*').as(Ops.Mul) ::
        Parser.char('/').as(Ops.Div) ::
        Nil,
    ) ~ (diceP.backtrack | number)

  def parserP = diceP ~ modiferP.rep0

  // Todo, ignore whitespace, remove recursiv
  val parseRoll: Parser[Exp] = Parser.recursive[Exp] { recurse =>
    parserP.map { parsed =>
      parsed._2.foldLeft(parsed._1: Exp) { case (acc, (op, exp)) =>
        Op(op, acc, exp)
      }
    }
  }

  def result(d: Exp): Int = d match
    case Number(v)         => v
    case Dice(count, size) => List.fill(count)(Random.between(1, size + 1)).sum
    case Op(op, left, right) =>
      op match
        case Ops.Add => result(left) + result(right)
        case Ops.Mul => result(left) * result(right)
        case Ops.Div => result(left) / result(right)
        case Ops.Sub => result(left) - result(right)
}
