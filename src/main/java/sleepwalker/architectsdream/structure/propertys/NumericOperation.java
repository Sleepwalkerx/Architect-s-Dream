package sleepwalker.architectsdream.structure.propertys;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NumericOperation {
    @Nonnull
    private final Number value;
    @Nonnull
    private final EnumNumberOperation operation;

    public NumericOperation(@Nonnull Number value, @Nonnull EnumNumberOperation operation){
        this.operation = operation;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericOperation that = (NumericOperation) o;
        return Objects.equals(value, that.value) && operation == that.operation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, operation);
    }

    @Nonnull
    public EnumNumberOperation getOperation() { return operation; }
    @Nonnull
    public Number getNumber(){ return value; }

    public boolean equals(Number value){
       return operation.is(this.value, value);
    }

    public enum EnumNumberOperation {
        EQUAL {
            @Override
            boolean is(Number a, Number b) {
                return a.equals(b);
            }
        },
        NOT_EQUAL {
            @Override
            boolean is(Number a, Number b) {
                return !a.equals(b);
            }
        },
        LARGE_OR_EQUAL {
            @Override
            boolean is(Number a, Number b) {
                return a.doubleValue() >= b.doubleValue();
            }
        },
        LARGE {
            @Override
            boolean is(Number a, Number b) {
                return a.doubleValue() > b.doubleValue();
            }
        },
        LESS_OR_EQUAL {
            @Override
            boolean is(Number a, Number b) {
                return a.doubleValue() <= b.doubleValue();
            }
        },
        LESS {
            @Override
            boolean is(Number a, Number b) {
                return a.doubleValue() < b.doubleValue();
            }
        };
    
        abstract boolean is(Number a, Number b);
    }
}

