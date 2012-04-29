/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package givertaker;

import java.util.ArrayList;
import java.awt.*;

import java.util.Random;
  

/**
 *
 * @author john
 */
public class Things {
  public class Spawn
  {
    public Spawn ()
    {
    }
    //#region GT
    public Random rand = new Random();//static 
    public class Org
    {
      public double E;
      //public Org CopyMe() { return (Org)this.MemberwiseClone(); }
    }
    public class PlaceHolder
    {
      public Org Ctr;
      public int MyDex;
      public PlaceHolder[] Nbrs = new PlaceHolder[8];
      public PlaceHolder GetFattestNbr()
      {
        PlaceHolder[] Besties = new PlaceHolder[8];
        double BestNbrE = -1.0; 
        int BestCnt=0;
        // Pick the fattest neighbor. If there is a tie, pick randomly from among the best.
        for (int nbrcnt = 0; nbrcnt < 8; nbrcnt++) {
          PlaceHolder ph = Nbrs [nbrcnt];
          Double sampleE;
          if (ph.Ctr == null) {
            sampleE = 0.0;
          } else {
            sampleE = ph.Ctr.E;
          }
          if (BestNbrE < sampleE) {
            BestCnt = 0;
            BestNbrE = sampleE;
            Besties [BestCnt] = ph;
          } else if (BestNbrE == sampleE) {
            BestCnt++;
            Besties [BestCnt] = ph;
          }
        }
        // now pick a random from Besties. 
        PlaceHolder MaxNbr = null;
        MaxNbr = Besties [rand.nextInt(BestCnt + 1)];
        return MaxNbr;
      }
      public double GetRegionE()
      {
        double NbrE = 0;
        for (int nbrcnt = 0; nbrcnt < 8; nbrcnt++)
        {
          PlaceHolder ph = Nbrs[nbrcnt];
          if (ph.Ctr != null) { NbrE += ph.Ctr.E; }
        }
        return NbrE;
      }
    }
    public class Grid2 extends ArrayList<PlaceHolder>
    {
      public int Sz;
      public int Wdt, Hgt;
      public void Init(int WdtNew, int HgtNew)
      {
        this.Wdt = WdtNew; this.Hgt = HgtNew;
        Sz = HgtNew * WdtNew;

        // fill cells
        for (int cnt = 0; cnt < Sz; cnt++)
        {
          PlaceHolder ph = new PlaceHolder();
          ph.MyDex = cnt;
          this.add(ph);
        }

        // connect here
        for (int Row = 0; Row < Hgt; Row++)
        {
          for (int Col = 0; Col < Wdt; Col++)
          {
            PlaceHolder ctr = this.Get(Col, Row);// get self

            int localcnt = 0;
            for (int Ycnt = -1; Ycnt <= 1; Ycnt++)
            {
              int NbrY = Row + Ycnt;
              if (NbrY < 0) { NbrY += Hgt; } else if (NbrY >= Hgt) { NbrY -= Hgt; }

              for (int Xcnt = -1; Xcnt <= 1; Xcnt++)
              {
                if ((Ycnt != 0) && (Xcnt != 0))
                {
                  int NbrX = Col + Xcnt;
                  if (NbrX < 0) { NbrX += Wdt; } else if (NbrX >= Wdt) { NbrX -= Wdt; }
                  ctr.Nbrs[localcnt] = this.Get(NbrX, NbrY);// get nbr
                  localcnt++;
                }
              }

            }
          }
        }
      }
      public PlaceHolder Get(int Col, int Row)
      {
        int Dex = Row * Wdt + Col;
        return this.get(Dex);
      }
      public PlaceHolder Get(int Dex)
      {
        return this.get(Dex);
      }
      public int GetSz() { return Sz; }
    }
    public void SingleGrid(Grid2 MyGrid)
    {// this is the copy part
      Double LThresh = 0.5;
      Double BThresh = LThresh + 0.5;
      ArrayList<PlaceHolder> SpawnList = new ArrayList<PlaceHolder>();
      ArrayList<PlaceHolder> DeathList = new ArrayList<PlaceHolder>();
      int Sz = MyGrid.GetSz();
      for (int cnt = 0; cnt < Sz; cnt++)
      {
        PlaceHolder ph0 = MyGrid.Get(cnt);
        if (ph0.Ctr != null)// if my grid cell full:
        {
          if (ph0.Ctr.E < LThresh)//if E not enough to live:
          {// mark for death
            DeathList.add(ph0);
          }
        }
        else //if my grid empty: 
        {
          double sum = ph0.GetRegionE();//take E of region in grid
          if (sum >= BThresh)
          {// mark cell for possible birth
            SpawnList.add(ph0);
          }
        }
      }
   
      // SpawnList.Sort(IComparer ) // sort by sum E here.

      for  (PlaceHolder ph : SpawnList)
      {//now go down list, for each
        double sum = ph.GetRegionE();//check all nbr E in grid0
        if (sum >= BThresh)// if still above bthresh 
        {
          Org Ctr = new Org(); // Birth
          MyGrid.Get(ph.MyDex).Ctr = Ctr;
/*
          bill nbrs;
          all nbrs or did we just get and charge the best?
          * 
          */
          /*
          we have to decide, do we scan and bill all of the parents or just the richest?
          
          richest is easier. if we bill all, we could bill some to death. 
          
          or we would only count the fat of those nbrs that goes *above* survival, and only bill from that.
          
          */
        }

      }
      
      for (PlaceHolder ph : DeathList)
      {//now go down list, for each
        // delete ph.Ctr
        ph.Ctr=null;
      }
    }
    //#endregion GT
  }
  
}


