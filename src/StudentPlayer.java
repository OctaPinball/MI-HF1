import static java.lang.Math.random;
import java.util.Random;

public class StudentPlayer extends Player{

    final static int MAX_COLUMN = 7;
    final static int MAX_ROW = 6;


    final static int MIDDLE_SCORE = 3;
    final static int WINNING_SCORE = 100;
    final static int THREE_SCORE = 5;
    final static int TWO_SCORE = 2;
    final static int OP_THREE_SCORE = -4;

    public class algResult{
        public int column;
        public int score;

        public algResult(int c, int s){
            column = c;
            score = s;
        }

        public algResult(int c, Board b){
            column = c;
            score = calculateScore(b);
        }

        public boolean closerToMiddle(int a, int b){
            return Math.abs(3 - a) <= Math.abs(3 - b);
        }

        public algResult max(algResult in, int column){
            if(this.score > in.score)
                return this;
            if(this.score == in.score)
            {
                if(closerToMiddle(this.column, column))
                    return this;
                else
                    return new algResult(column, in.score);
            }
            return new algResult(column, in.score);
        }
        public int max(int in){
            return Math.max(this.score, in);
        }

        public algResult min(algResult in, int column){
            if(this.score < in.score)
                return this;
            if(this.score == in.score)
            {
                if(closerToMiddle(this.column, column))
                    return this;
                else
                    return new algResult(column, in.score);
            }
            return new algResult(column, in.score);
        }
        public int min(int in){
            return Math.min(this.score, in);
        }

        public int countFour(int[] four, int player){
            int count = 0;
            for(int i = 0; i < 4; i++)
            {
                if(four[i] == player) {
                    count++;
                }
            }
            return count;
        }

        public int calculateFour(int[] four){
            int score = 0;


            if(countFour(four, StudentPlayer.playerIndex) == 3 && countFour(four, 0) == 1)
                score += THREE_SCORE;
            if(countFour(four, StudentPlayer.playerIndex) == 2 && countFour(four, 0) == 2)
                score += TWO_SCORE;

            if(countFour(four, getOtherPlayerIndex(StudentPlayer.playerIndex)) == 3 && countFour(four, 0) == 1)
                score += OP_THREE_SCORE;

            return score;

        }

        public int calculateScore(Board board){
            int score = 0;

            int[][] table = board.getState();

            if(board.getWinner() == StudentPlayer.playerIndex)
            {
                score += WINNING_SCORE;
            }
            if(board.getWinner() == getOtherPlayerIndex(StudentPlayer.playerIndex))
            {
                score -= WINNING_SCORE;
            }

            for(int i = MAX_ROW - 1; i >= 0; i--)
            {
                if(table[i][(int)Math.floor(MAX_COLUMN /2)] == StudentPlayer.playerIndex) {
                    score += MIDDLE_SCORE;
                }
                if(table[i][(int)Math.floor(MAX_COLUMN /2)] == 0) {
                    break;
                }
            }

            //Vizszintes
            for(int i = 0; i < MAX_ROW; i++)
            {
                for(int j = 0; j < MAX_COLUMN - 3; j++)
                {
                    int[] four = new int[4];
                    for(int k = 0; k < 4; k++)
                    {
                        four[k] = table[i][k + j];
                    }
                    score += calculateFour(four);
                }
            }

            //Fuggoleges
            for(int i = 0; i < MAX_COLUMN; i++)
            {
                for(int j = 0; j < MAX_ROW - 3; j++)
                {
                    int[] four = new int[4];
                    for(int k = 0; k < 4; k++)
                    {
                        four[k] = table[k + j][i];
                    }
                    score += calculateFour(four);
                }
            }

            //Atlos 1
            for(int i = 0; i < MAX_ROW - 3; i++)
            {
                for(int j = 0; j < MAX_COLUMN - 3; j++)
                {
                    int[] four = new int[4];
                    for(int k = 0; k < 4; k++)
                    {
                        four[k] = table[k + i][k + j];
                    }
                    score += calculateFour(four);
                }
            }

            //Atlos 2
            for(int i = 0; i < MAX_ROW - 3; i++)
            {
                for(int j = 0; j < MAX_COLUMN - 3; j++)
                {
                    int[] four = new int[4];
                    for(int k = 0; k < 4; k++)
                    {
                        four[k] = table[i - k + 3][k + j];
                    }
                    score += calculateFour(four);
                }
            }
            return score;
        }

        public int getOtherPlayerIndex(int playerIndex){
            if(playerIndex == 1)
                return 2;
            return 1;
        }
    }



    public algResult minimax(Board board, int depth, int alpha, int beta, boolean maxPlayer){
        if(depth == 0 || board.gameEnded())
        {
            return new algResult(-1, board);
        }

        if(maxPlayer)
        {
            algResult maxResult = new algResult(-1, -1000);
            for(int i = 0; i < 7; i++)
            {
                Board newBoard = new Board(board);
                if(newBoard.stepIsValid(i)) {
                    newBoard.step(this.playerIndex, i);
                    algResult result = minimax(newBoard, depth - 1, alpha, beta, false);
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
            algResult minResult = new algResult(-1, 1000);
            for(int i = 0; i < 7; i++)
            {
                Board newBoard = new Board(board);
                if(newBoard.stepIsValid(i)) {
                    newBoard.step(getOtherPlayerIndex(), i);
                    algResult result = minimax(newBoard, depth - 1, alpha, beta, true);
                    minResult = minResult.min(result, i);
                    beta = result.min(beta);
                    if (beta <= alpha)
                        break;
                }
            }
            return minResult;
        }
    }

    public static int playerIndex = 2;

    //public MiniMaxAlphaBetaPruningPlayer modle;

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
        //modle = new MiniMaxAlphaBetaPruningPlayer(int playerIndex, int[] boardSize, int nToConnect, 7);
    }

    public int getOtherPlayerIndex(){
        if(this.playerIndex == 1)
            return 2;
        return 1;
    }



    @Override
    public int step(Board board) {
        Random random = new Random();
        int randomnum = random.nextInt(2);
        algResult res = null;
        if(randomnum == 1)
            res = minimax(board, 11, -1000, 1000, true);
        if(randomnum == 0)
            res = minimax(board, 10, -1000, 1000, true);
        //return modle.step(board);
        return res.column;
    }
}
