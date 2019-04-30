package mysticalmechanics.api;

public interface IMechUnit<T extends Comparable> {
    String getName();

    int getPriority();

    double getZero();

    double getNeutral();

    double convertToPower(T unit);

    T convertToUnit(double power);

    String format(double power);
}
