public class StudentPlayer extends Player{

    public class algResult{
        public int collum;
        public int score;

        public algResult(int c, int s){
            collum = c;
            score = s;
        }

        public algResult(int c, Board b, int playerindex){
            collum = c;
            score = -1;
            if(b.getWinner() < 1)
                score = 0;
            if(b.getWinner() == playerindex)
                score = 1;
        }

        public boolean closerToMiddle(int a, int b){
            return Math.abs(3 - a) <= Math.abs(3 - b);
        }

        public algResult max(algResult in, int collum){
            if(this.score > in.score)
                return this;
            if(this.score == in.score)
            {
                if(closerToMiddle(this.collum, collum))
                    return this;
                else
                    return new algResult(collum, in.score);
            }
            return new algResult(collum, in.score);
        }
        public int max(int in){
            if(this.score >= in)
                return this.score;
            return in;
        }

        public algResult min(algResult in, int collum){
            if(this.score < in.score)
                return this;
            if(this.score == in.score)
            {
                if(closerToMiddle(this.collum, collum))
                    return this;
                else
                    return new algResult(collum, in.score);
            }
            return new algResult(collum, in.score);
        }
        public int min(int in){
            if(this.score <= in)
                return this.score;
            return in;
        }
    }

    public algResult minimax(Board board, int depth, int alpha, int beta, boolean maxPlayer){
        if(depth == 0 || board.gameEnded())
        {
            return new algResult(-1, board, this.playerIndex);
        }

        if(maxPlayer)
        {
            algResult maxResult = new algResult(-1, -100);
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
            algResult minResult = new algResult(-1, 100);
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
        algResult res = minimax(board, 12, -100, 100, true);
        return res.collum;
    }
}
