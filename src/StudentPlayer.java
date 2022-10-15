import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentPlayer extends Player{

    public static final int MAX_DEPTH = 16;

    public class TranspositionTable{
        public int[][] table = new int[7][6];
        public int score;

        public TranspositionTable(TranspositionTable in){
            this.score = in.score;
            this.table = in.table.clone();
        }

        public TranspositionTable(){
            this.score = 0;
            this.table = new int[7][6];
        }

        public void Put(int collum, int player){
            for(int i = 0; i < 6; i++)
            {
                if(table[collum][i] == 0)
                {
                    table[collum][i] = player;
                    return;
                }
            }
        }

        public boolean Equals(TranspositionTable in){
            for(int i = 0; i < 7; i++)
            {
                for(int j = 0; j < 6; j++)
                {
                    if(this.table[i][j] != in.table[i][j])
                    {
                        return false;
                    }
                    if(this.table[i][j] == 0)
                    {
                        break;
                    }
                }
            }
            return true;
        }
    }

    public class AlgResult {
        public int collum;
        public int score;

        public AlgResult(int c, int s){
            collum = c;
            score = s;
        }

        public AlgResult(int c, Board b, int playerindex){
            collum = c;
            score = -1;
            if(b.getWinner() < 1)
                score = 0;
            if(b.getWinner() == playerindex)
                score = 1;
        }

        public boolean closerToMiddle(int a, int b){
            return Math.abs(0 - a) <= Math.abs(0 - b);
        }

        public AlgResult max(AlgResult in, int collum){
            if(this.score > in.score)
                return this;
            if(this.score == in.score)
            {
                if(closerToMiddle(this.collum, collum))
                    return this;
                else
                    return new AlgResult(collum, in.score);
            }
            return new AlgResult(collum, in.score);
        }
        public int max(int in){
            if(this.score >= in)
                return this.score;
            return in;
        }

        public AlgResult min(AlgResult in, int collum){
            if(this.score < in.score)
                return this;
            if(this.score == in.score)
            {
                if(closerToMiddle(this.collum, collum))
                    return this;
                else
                    return new AlgResult(collum, in.score);
            }
            return new AlgResult(collum, in.score);
        }
        public int min(int in){
            if(this.score <= in)
                return this.score;
            return in;
        }
    }

    public HashMap<Integer, List<TranspositionTable>> transpositiontable;

    public TranspositionTable tableExist(TranspositionTable table, int depth){
        List<TranspositionTable> tablecollection = transpositiontable.get(depth);
        if(tablecollection == null) {
            tablecollection = new ArrayList<TranspositionTable>();
            return null;
        }
        for(TranspositionTable t : tablecollection)
        {
            if(table.Equals(t))
            {
                return t;
            }
        }
        return null;
    }

    public AlgResult minimax(Board board, int depth, int alpha, int beta, boolean maxPlayer, TranspositionTable table){
        if(depth == 0 || board.gameEnded())
        {
            return new AlgResult(-1, board, this.playerIndex);
        }

        if(maxPlayer)
        {
            AlgResult maxResult = new AlgResult(-1, -100);
            for(int i = 0; i < 7; i++)
            {
                Board newBoard = new Board(board);
                if(newBoard.stepIsValid(i)) {
                    newBoard.step(this.playerIndex, i);

                    TranspositionTable newtable = new TranspositionTable(table);
                    newtable.Put(i, this.playerIndex);

                    AlgResult result;
                    TranspositionTable tablepair = tableExist(newtable, depth);
                    if(tablepair != null) {
                        result = new AlgResult(i, tablepair.score);
                    }
                    else {
                        result = minimax(newBoard, depth - 1, alpha, beta, false, newtable);
                        newtable.score = result.score;
                        transpositiontable.get(i).add(newtable);
                    }


                    maxResult = maxResult.max(result, i);
                    alpha = result.max(alpha);
                    if (beta <= alpha)
                        break;
                }
            }
            return maxResult;
        }

        else
        {
            AlgResult minResult = new AlgResult(-1, 100);
            for(int i = 0; i < 7; i++)
            {
                Board newBoard = new Board(board);
                if(newBoard.stepIsValid(i)) {
                    newBoard.step(getOtherPlayerIndex(), i);

                    TranspositionTable newtable = new TranspositionTable(table);
                    newtable.Put(i, this.playerIndex);

                    AlgResult result;
                    TranspositionTable tablepair = tableExist(newtable, depth);
                    if(tablepair != null) {
                        result = new AlgResult(i, tablepair.score);
                    }
                    else {
                        result = minimax(newBoard, depth - 1, alpha, beta, true, newtable);
                        newtable.score = result.score;
                        transpositiontable.get(i).add(newtable);
                    }

                    minResult = minResult.min(result, i);
                    beta = result.min(beta);
                    if (beta <= alpha)
                        break;
                }
            }
            return minResult;
        }
    }

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    public int getOtherPlayerIndex(){
        if(this.playerIndex == 1)
            return 2;
        return 1;
    }



    @Override
    public int step(Board board) {
        transpositiontable = new HashMap<Integer, List<TranspositionTable>>();
        for(int i = 0; i < MAX_DEPTH; i++)
        {
            transpositiontable.put(i,new ArrayList<TranspositionTable>());
        }

        TranspositionTable table = new TranspositionTable();

        AlgResult res = minimax(board, MAX_DEPTH, -100, 100, true, table);
        return res.collum;
    }
}
