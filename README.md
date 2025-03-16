# Intro
ZeroLambda is 100% pure functional programming language which will allow you to code in Untyped Lambda Calculus as defined in textbooks [^1]. I dedicate it to Turing and Church and their groundbreaking work in the foundations of computation.

1. You will always code in pure low level lambdas
2. You will have to build every primitive from scratch (numbers, lists, pairs, recursion, addition, boolean logic etc). You can refer to [Church encoding](https://en.wikipedia.org/wiki/Church_encoding) for the full list of primitives and how to encode them
3. ZeroLambda is an educational project that will help you to learn and understand any other functional programming language
4. There is nothing hidden from you. You give a big lambda to the lambda machine and you have a normalized lambda back
5. ZeroLambda is turing complete because Untyped Lambda Calculus (ULC) is turing complete. Moreover, the ULC is an alternative model of computation which will change the way you think
6. You can see any other functional programming language as ZeroLambda with many technical optimizations (e.g. number multiplication) and restrictions on beta reductions (e.g. if we add types)
7. The deep secrets of functional programming will be delivered to you very fast

# You need ZeroLambda if
1. You struggle to see the clear border between functional programming and other paradigms
2. You don't understand how and why Fixpoint and Y-Combinator work and you need them in theorem provers like Coq and Lean to deeply understand the foundations of mathematics
3. You use functional programming in a language where it was added later (like in Java) and not a part of the core (which is very bad for understanding)
    + You work with Java Streams or Project Reactor (reactive programming reuses functional paradigm a lot)
4. You code in almost pure programming languages like Haskell or OCaml and think they keep many things hidden from you and it slows down your coding performance or motivation
5. You don't understand what the model of computation is and why lambdas can compute
6. You prepare for big tech interviews and would like to understand functional algorithms better because there are many algorithms that can be implemented fully functional and it is much faster, easier and cleaner to code (e.g. MergeSort)

# Examples
How to calculate factorial

    plus := λm.λn.λf.λx.m f(n f x)
    mult := λm.λn.λf.λx.m (n f) x
    zero := λf.λx.x
    one    := λf.λx.f x
    two    := λf.λx.f (f x)
    three  := λf.λx.f (f (f x))
    four   := λf.λx.f (f (f (f x)))
    five   := λf.λx.f (f (f (f (f x))))
    pred := λn.λf.λx.n(λk.λh.h(k f))(λu.x)(λu.u)
    if := λb.λx.λy.(b x) y
    true := λx.λy.x
    false := λx.λy.y
    isZero := λn.n(λx.false)true
    yCombinator := λy . (λx . y(x x))(λx . y(x x))
    factorial := yCombinator (λg.λn.if(isZero n)(one)(mult n(g(pred n))))
    factorial five --120

If you need more examples, you can refer to [NinetyNineHaskellProblems.ulc](src/main/ulc/NinetyNineHaskellProblems.ulc). I've implemented 13/99 problems in ZeroLambda as a demonstration

# How to start
1. Install Intellij Idea Community Edition and open the project in it
3. Run "java iaroslav.baranov.zerolambda.app.ReplApp [sourceCode.ulc]"
4. You can either edit source code in *.ulc or just type it in REPL
5. You can use any global hotkey automation tool to assign "λ" to "ctrl + L" for faster typing

# Tips
If you want step by step visualization of how lambdas compute, you can use [lambdacalc.io](https://lambdacalc.io/)

[^1]: Type Theory and Formal Proof: An Introduction 1st Edition by Rob Nederpelt and Herman Geuvers
