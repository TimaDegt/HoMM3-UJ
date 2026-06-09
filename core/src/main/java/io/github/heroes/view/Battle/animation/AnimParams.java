package io.github.heroes.view.Battle.animation;

public class AnimParams {
    private final int spriteSize;
    private final int deathAnimIndex;
    private final int deathAnimLength;
    private final int receiveDmgAnimIndex;
    private final int receiveDmgAnimLength;
    private final int attackAnimIndex;
    private final int attackAnimLength;
    private final int rangeAttackAnimIndex;
    private final int rangeAttackAnimLength;
    private final int moveAnimIndex;
    private final int moveAnimLength;
    private final int idleIndex;
    private final int randomAnimIndex;
    private final int randomAnimLength;

    public enum AnimType {
        IDLE,
        MOVE,
        ATTACK,
        RANGEATTACK,
        RECEIVEDMG,
        DEATH,
        DEAD,
        RANDOMANIM;
    }

    public AnimParams(int spriteSize, int deathAnimIndex, int deathAnimLength, int receiveDmgAnimIndex, int receiveDmgAnimLength,
                      int attackAnimIndex, int attackAnimLength, int rangeAttackAnimIndex, int rangeAttackAnimLength,
                      int moveAnimIndex, int moveAnimLength, int idleIndex, int randomAnimIndex, int randomAnimLength) {
        this.spriteSize = spriteSize;
        this.deathAnimIndex = deathAnimIndex;
        this.deathAnimLength = deathAnimLength;
        this.receiveDmgAnimIndex = receiveDmgAnimIndex;
        this.receiveDmgAnimLength = receiveDmgAnimLength;
        this.attackAnimIndex = attackAnimIndex;
        this.attackAnimLength = attackAnimLength;
        this.rangeAttackAnimIndex = rangeAttackAnimIndex;
        this.rangeAttackAnimLength = rangeAttackAnimLength;
        this.moveAnimIndex = moveAnimIndex;
        this.moveAnimLength = moveAnimLength;
        this.idleIndex = idleIndex;
        this.randomAnimIndex = randomAnimIndex;
        this.randomAnimLength = randomAnimLength;
    }

    public int currentCoordinate(AnimType animType) {
        if (animType == AnimType.IDLE) {
            return spriteSize * idleIndex;
        }
        if (animType == AnimType.MOVE) {
            return spriteSize * moveAnimIndex;
        }
        if (animType == AnimType.ATTACK) {
            return spriteSize * attackAnimIndex;
        }
        if (animType == AnimType.RANGEATTACK) {
            return spriteSize * rangeAttackAnimIndex;
        }
        if (animType == AnimType.RECEIVEDMG) {
            return spriteSize * receiveDmgAnimIndex;
        }
        if (animType == AnimType.DEATH) {
            return spriteSize * deathAnimIndex;
        }
        if (animType == AnimType.DEAD) {
            return spriteSize * deathAnimIndex;
        }
        if (animType == AnimType.RANDOMANIM) {
            return spriteSize * randomAnimIndex;
        }
        return 0;
    }

    public int currentLength(AnimType animType) {
        if (animType == AnimType.IDLE) {
            return 0;
        }
        if (animType == AnimType.MOVE) {
            return moveAnimLength;
        }
        if (animType == AnimType.ATTACK) {
            return attackAnimLength;
        }
        if (animType == AnimType.RANGEATTACK) {
            return rangeAttackAnimLength;
        }
        if (animType == AnimType.RECEIVEDMG) {
            return receiveDmgAnimLength;
        }
        if (animType == AnimType.DEATH) {
            return deathAnimLength;
        }
        if (animType == AnimType.DEAD) {
            return deathAnimLength;
        }
        if (animType == AnimType.RANDOMANIM) {
            return randomAnimLength;
        }
        return 0;
    }

    public int getSize() {
        return spriteSize;
    }
}
