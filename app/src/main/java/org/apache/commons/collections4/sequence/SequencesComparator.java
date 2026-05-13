package org.apache.commons.collections4.sequence;

import java.util.List;
import org.apache.commons.collections4.Equator;
import org.apache.commons.collections4.functors.DefaultEquator;

/* JADX INFO: loaded from: classes.dex */
public class SequencesComparator<T> {
    private final Equator<? super T> equator;
    private final List<T> sequence1;
    private final List<T> sequence2;
    private final int[] vDown;
    private final int[] vUp;

    public SequencesComparator(List<T> sequence1, List<T> sequence2) {
        this(sequence1, sequence2, DefaultEquator.defaultEquator());
    }

    public SequencesComparator(List<T> sequence1, List<T> sequence2, Equator<? super T> equator) {
        this.sequence1 = sequence1;
        this.sequence2 = sequence2;
        this.equator = equator;
        int size = sequence1.size() + sequence2.size() + 2;
        this.vDown = new int[size];
        this.vUp = new int[size];
    }

    public EditScript<T> getScript() {
        EditScript<T> script = new EditScript<>();
        buildScript(0, this.sequence1.size(), 0, this.sequence2.size(), script);
        return script;
    }

    private Snake buildSnake(int i, int i2, int i3, int i4) {
        int i5 = i;
        while (i5 - i2 < i4 && i5 < i3 && this.equator.equate(this.sequence1.get(i5), this.sequence2.get(i5 - i2))) {
            i5++;
        }
        return new Snake(i, i5, i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00ed  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private org.apache.commons.collections4.sequence.SequencesComparator.Snake getMiddleSnake(int r20, int r21, int r22, int r23) {
        /*
            Method dump skipped, instruction units count: 351
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.commons.collections4.sequence.SequencesComparator.getMiddleSnake(int, int, int, int):org.apache.commons.collections4.sequence.SequencesComparator$Snake");
    }

    private void buildScript(int i, int i2, int i3, int i4, EditScript<T> editScript) {
        Snake middleSnake = getMiddleSnake(i, i2, i3, i4);
        if (middleSnake == null || ((middleSnake.getStart() == i2 && middleSnake.getDiag() == i2 - i4) || (middleSnake.getEnd() == i && middleSnake.getDiag() == i - i3))) {
            int i5 = i;
            int i6 = i3;
            while (true) {
                if (i5 < i2 || i6 < i4) {
                    if (i5 < i2 && i6 < i4 && this.equator.equate(this.sequence1.get(i5), this.sequence2.get(i6))) {
                        editScript.append(new KeepCommand<>(this.sequence1.get(i5)));
                        i5++;
                        i6++;
                    } else if (i2 - i > i4 - i3) {
                        editScript.append(new DeleteCommand<>(this.sequence1.get(i5)));
                        i5++;
                    } else {
                        editScript.append(new InsertCommand<>(this.sequence2.get(i6)));
                        i6++;
                    }
                } else {
                    return;
                }
            }
        } else {
            buildScript(i, middleSnake.getStart(), i3, middleSnake.getStart() - middleSnake.getDiag(), editScript);
            for (int start = middleSnake.getStart(); start < middleSnake.getEnd(); start++) {
                editScript.append(new KeepCommand<>(this.sequence1.get(start)));
            }
            buildScript(middleSnake.getEnd(), i2, middleSnake.getEnd() - middleSnake.getDiag(), i4, editScript);
        }
    }

    private static class Snake {
        private final int diag;
        private final int end;
        private final int start;

        public Snake(int start, int end, int diag) {
            this.start = start;
            this.end = end;
            this.diag = diag;
        }

        public int getStart() {
            return this.start;
        }

        public int getEnd() {
            return this.end;
        }

        public int getDiag() {
            return this.diag;
        }
    }
}
