package iaroslav.baranov.zerolambda;

public class Terms {
    public static Term MULTIPLICATION = new Abstraction(new Abstraction(
            new Abstraction(new Abstraction(
                    new Application(new Application(4,
                            new Application(3, 2)), 1)))));

    public static Term TRUE = new Abstraction(new Abstraction(new Variable(2)));
    public static Term FALSE = new Abstraction(new Abstraction(new Variable(1)));

    public static Term ZERO = new Abstraction(new Abstraction(new Variable(1)));
    public static Term ONE = new Abstraction(new Abstraction(
            new Application(2, new Variable(1))
    ));
    public static Term TWO = new Abstraction( new Abstraction(
            new Application(2, new Application(2, new Variable(1)))
    ));
    public static Term THREE = new Abstraction(new Abstraction(
            new Application(2, new Application(2, new Application(2, new Variable(1))))
    ));

    public static Term IS_ZERO = new Abstraction(new Application(
            new Application(1, new Abstraction(FALSE))
            , TRUE));

    public static Term IF =
            new Abstraction(
                    new Abstraction(
                            new Abstraction(
                                    new Application(new Application(3, 2), 1))));

    //PRED=λn.λf.λx.n(λg.λh.h(g f))(λu.x)(λu.u).
    public static Term PRED = new Abstraction(new Abstraction(
            new Abstraction(new Application(new Application(
                    new Application(3,
                            new Abstraction(new Abstraction(
              new Application(1, new Application(2, 4))))),
                    new Abstraction(new Variable(2))),
                    new Abstraction(new Variable(1))))));

    //Y ≡ λy . (λx . y(x x))(λx . y(x x))
    public static Term Y_COMBINATOR = new Abstraction(
            new Application(
                    new Abstraction(new Application(2, new Application(1, 1))),
                    new Abstraction(new Application(2, new Application(1, 1)))
            ));

    //Z = λy . (λx.y(λv.xxv))(λx.y(λv.xxv))
    public static Term Z_COMBINATOR = new Abstraction(
            new Application(
                    new Abstraction(new Application(2, new Abstraction(new Application(new Application(2, 2),1)))),
                    new Abstraction(new Application(2, new Abstraction(new Application(new Application(2, 2), 1))))
            ));

}
