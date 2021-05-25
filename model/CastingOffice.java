package model;

// CastingOffice.java
// A location where players may upgrade their rank for dollars or credits

public class CastingOffice extends Location {
    private static int[] costCredits = new int[]{5,10,15,20,25};
    private static int[] costDollars = new int[]{4,10,18,28,40};

    public CastingOffice () {
        super("Casting Office");
    }

    //Player requests CastingOffice for cost to upgrade rank, providing
    //desired rank and currency type > true = dollars | false = credits
    // subracting two from rank compensates for (1): zero indexing and (2): rank 1 not being included in the cost chart
    public static int getCost(int rank, boolean dollars){
        if(dollars){
            return costDollars[rank-2];
        }
        return costCredits[rank-2];
    }
}