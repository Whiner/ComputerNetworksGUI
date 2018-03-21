package NodeGenerator;

import java.util.Random;

public enum Direction {

    None{
        @Override
        public Direction reverse() {
            return None;
        }
    },
    Up{
        @Override
        public Direction reverse() {
            return Down;
        }
    },
    Up_Right{
        @Override
        public Direction reverse() {
            return Left_Down;
        }
    },
    Right{
        @Override
        public Direction reverse() {
            return Left;
        }
    },
    Right_Down{
        @Override
        public Direction reverse() {
            return Up_Left;
        }
    },
    Down{
        @Override
        public Direction reverse() {
            return Up;
        }
    },
    Left_Down{
        @Override
        public Direction reverse() {
            return Up_Right;
        }
    },
    Left{
        @Override
        public Direction reverse() {
            return Right;
        }
    },
    Up_Left{
        @Override
        public Direction reverse() {
            return Right_Down;
        }
    };
    public abstract Direction reverse();
    public static Direction RandomDirection(){
        return Direction.values()[new Random().nextInt(8) + 1];
    }

    public static Direction CheckDirection(Node from, Node to){
        if(from.equals(to))
            return None;
        if (from.getCellNumber_X() < to.getCellNumber_X()
                && from.getCellNumber_Y() == to.getCellNumber_Y())
            return Right;
        if (from.getCellNumber_X() < to.getCellNumber_X()
                && from.getCellNumber_Y() < to.getCellNumber_Y())
            return Right_Down;
        if (from.getCellNumber_X() == to.getCellNumber_X()
                && from.getCellNumber_Y() < to.getCellNumber_Y())
            return Down;
        if (from.getCellNumber_X() > to.getCellNumber_X()
                && from.getCellNumber_Y() < to.getCellNumber_Y())
            return Left_Down;
        if (from.getCellNumber_X() > to.getCellNumber_X()
                && from.getCellNumber_Y() == to.getCellNumber_Y())
            return Left;
        if (from.getCellNumber_X() > to.getCellNumber_X()
                && from.getCellNumber_Y() > to.getCellNumber_Y())
            return Up_Left;
        if (from.getCellNumber_X() == to.getCellNumber_X()
                && from.getCellNumber_Y() > to.getCellNumber_Y())
            return Up;
        if (from.getCellNumber_X() < to.getCellNumber_X()
                && from.getCellNumber_Y() > to.getCellNumber_Y())
            return Up_Right;

        return null;
    }
    public static int Check_X_by_Direction(Node from, Direction direction){
        if(direction == Up_Left
                || direction == Left
                || direction == Left_Down)
            return from.getCellNumber_X() - 1;
        else
            if(direction == Up_Right
                || direction == Right
                || direction == Right_Down)
            return from.getCellNumber_X() + 1;
        else
            return from.getCellNumber_X();
    }
    public static int Check_Y_by_Direction(Node from, Direction direction){
        if(direction == Up_Left
                || direction == Up
                || direction == Up_Right)
            return from.getCellNumber_Y() - 1;
        else
        if(direction == Right_Down
                || direction == Down
                || direction == Left_Down)
            return from.getCellNumber_Y() + 1;
        else
            return from.getCellNumber_Y();
    }
}
