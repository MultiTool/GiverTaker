/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package givertaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import java.awt.*;

import java.util.*;

/**
 *
 * @author MultiTool
 */
public class Things {
  public Things() {
  }
  //#region GT
  public enum OrgType {
    Giver, Taker
  };
  public static final int GiverType = 0, TakerType = 1, NumTypes = 2;
  public Random rand = new Random();//static 
  public static final Double LThresh = 0.5;
  public static final Double BThresh = LThresh + 0.5;
  public static class Org {
    public int MyType; // OrgType MyType;
    public double E;
    public double GetSpareE() {// food above survival level
      return this.E - LThresh;
    }
    public boolean IsMoribund() {
      return this.E < LThresh;//if E not enough to live
    }
    public Org CopyMe() {
      try {
        return (Org) this.clone();
      } catch (Exception ex) {
        return null;
      }
    }
  }
  public static class Giver extends Org {
    @Override
    public double GetSpareE() {// food above survival level
      return this.E - LThresh;
    }
    @Override
    public Org CopyMe() {
      try {
        return (Giver) this.clone();
      } catch (Exception ex) {
        return null;
      }
    }
  };
  public static class Taker extends Org {
    @Override
    public double GetSpareE() {// food above survival level
      return this.E - LThresh;
    }
    @Override
    public Org CopyMe() {
      try {
        return (Taker) this.clone();
      } catch (Exception ex) {
        return null;
      }
    }
  };
  public static final Org[] OrgMaker = new Org[]{
    new Giver(), new Taker()
  };
  // private static final HashMap<Integer, OrgType> Num2Type = new HashMap<Integer, OrgType>();
  //private static final OrgType[] Num2Type = new OrgType[]{OrgType.Giver,OrgType.Taker};
  //private static final HashMap<OrgType,Integer > Type2Num = new HashMap<OrgType,Integer >();
  /* *************************************************************************************************** */
  public class PlaceHolder {
    public Org Ctr;
    public int MyDex;
    public PlaceHolder FattestNbr;
    public double NbrHoodE = 0.0;
    public PlaceHolder[] Nbrs = new PlaceHolder[8];
    /* *************************************************************************************************** */
    public PlaceHolder GetFattestNbr() {
      PlaceHolder BestNbr = null;
      double BestNbrE = -1.0;
      int BestCnt = 0;
      // Pick the fattest neighbor. If there is a tie, pick randomly from among the best.
      for (int nbrcnt = 0; nbrcnt < 8; nbrcnt++) {
        PlaceHolder ph = Nbrs[nbrcnt];
        double sampleE;
        sampleE = (ph.Ctr == null) ? 0.0 : ph.Ctr.GetSpareE();
        if (BestNbrE < sampleE) {
          BestCnt = 1;
          BestNbr = ph;
          BestNbrE = sampleE;
        } else if (BestNbrE == sampleE) {
          BestCnt++;
          double Odds = 1.0 / ((double) BestCnt);
          if (rand.nextDouble() < Odds) { // In the case of a tie, pick randomly.
            BestNbrE = sampleE;
            BestNbr = ph;
          }
        }
      }
      this.NbrHoodE = BestNbrE;// side effect! inelegant.
      this.FattestNbr = BestNbr;
      return BestNbr;
    }
    /* *************************************************************************************************** */
    public boolean IsFertile() {
      if (NbrHoodE >= BThresh) {// wrong!! work this out right.
        return true;
      }
      return false;
    }
    /* *************************************************************************************************** */
    public double GetRegionE() {
      double NbrE = 0;
      for (int nbrcnt = 0; nbrcnt < 8; nbrcnt++) {
        PlaceHolder ph = Nbrs[nbrcnt];
        if (ph.Ctr != null) {
          NbrE += ph.Ctr.E;
        }
      }
      this.NbrHoodE = NbrE;
      return NbrE;
    }
    /* *************************************************************************************************** */
    public double Spawn() {
      double[] NbrEList = new double[NumTypes];
      for (int nbrcnt = 0; nbrcnt < 8; nbrcnt++) {
        PlaceHolder PhNbr = Nbrs[nbrcnt];
        if (PhNbr.Ctr != null) {
          NbrEList[PhNbr.Ctr.MyType] += PhNbr.Ctr.E;
        }
      }

      int FattestType = 0;
      double MaxFat = -1.0;
      for (int tcnt = 0; tcnt < NumTypes; tcnt++) {
        if (MaxFat < NbrEList[tcnt]) {
          MaxFat = NbrEList[tcnt];
          FattestType = tcnt;
        }
      }

      if ((MaxFat - LThresh) >= BThresh) {
        // 
      }

      return MaxFat;
    }
  }
  /* *************************************************************************************************** */
  public class GridWorld extends ArrayList<PlaceHolder> {
    public int Sz;
    public int Wdt, Hgt;
    /* *************************************************************************************************** */
    public void Init(int WdtNew, int HgtNew) {
      this.Wdt = WdtNew;
      this.Hgt = HgtNew;
      Sz = HgtNew * WdtNew;

      // fill cells
      for (int cnt = 0; cnt < Sz; cnt++) {
        PlaceHolder ph = new PlaceHolder();
        ph.MyDex = cnt;
        this.add(ph);
      }

      // connect here
      for (int Row = 0; Row < Hgt; Row++) {
        for (int Col = 0; Col < Wdt; Col++) {
          PlaceHolder ctr = this.Get(Col, Row);// get self

          int localcnt = 0;
          for (int Ycnt = -1; Ycnt <= 1; Ycnt++) {
            int NbrY = Row + Ycnt;
            if (NbrY < 0) {
              NbrY += Hgt;
            } else if (NbrY >= Hgt) {
              NbrY -= Hgt;
            }

            for (int Xcnt = -1; Xcnt <= 1; Xcnt++) {
              if ((Ycnt != 0) && (Xcnt != 0)) {
                int NbrX = Col + Xcnt;
                if (NbrX < 0) {
                  NbrX += Wdt;
                } else if (NbrX >= Wdt) {
                  NbrX -= Wdt;
                }
                ctr.Nbrs[localcnt] = this.Get(NbrX, NbrY);// get nbr
                localcnt++;
              }
            }

          }
        }
      }
    }
    public PlaceHolder Get(int Col, int Row) {
      int Dex = Row * Wdt + Col;
      return this.get(Dex);
    }
    public PlaceHolder Get(int Dex) {
      return this.get(Dex);
    }
    public int GetSz() {
      return Sz;
    }
  }
  /* *************************************************************************************************** */
  public void SingleGrid(GridWorld MyGrid) {// this is the copy part
    ArrayList<PlaceHolder> BirthList = new ArrayList<PlaceHolder>();
    ArrayList<PlaceHolder> DeathList = new ArrayList<PlaceHolder>();
    int Sz = MyGrid.GetSz();
    for (int cnt = 0; cnt < Sz; cnt++) {
      PlaceHolder ph0 = MyGrid.Get(cnt);
      if (ph0.Ctr != null)// if my grid cell full:
      {
        if (ph0.Ctr.IsMoribund()) {//if E not enough to live
          DeathList.add(ph0);// mark for death
        }
      } else {//if this grid cell empty
        // double sum = ph0.GetRegionE();//take E of region in grid
        ph0.GetFattestNbr();//take E of region in grid
        if (ph0.IsFertile()) {// mark cell for possible birth
          BirthList.add(ph0);
        }
      }
    }

    Collections.shuffle(BirthList);// randomize to prevent spatial bias in birth order
    Collections.sort(BirthList, new Comparator() {// sort by sum E here.
      @Override
      public int compare(Object o1, Object o2) {
        PlaceHolder s1 = (PlaceHolder) o1;
        PlaceHolder s2 = (PlaceHolder) o2;
        return -Double.compare(s1.NbrHoodE, s2.NbrHoodE);// sort descending
      }
    });

    for (PlaceHolder ph : BirthList) {//now go down list, for each
      double sum = ph.GetRegionE();//check all nbr E in grid0
      if (sum >= BThresh)// if still above bthresh 
      {
        Org Ctr = new Org(); // Birth
        MyGrid.Get(ph.MyDex).Ctr = Ctr;
        /*
         bill nbrs;
         all nbrs or did we just get and charge the best?
        
         we have to decide, do we scan and bill all of the parents or just the richest?

         richest is easier. if we bill all, we could bill some to death. 

         or we would only count the fat of those nbrs that goes *above* survival, and only bill from that.

         */
      }

    }

    for (PlaceHolder ph : DeathList) {//now go down list, for each
      // delete ph.Ctr
      ph.Ctr = null;
    }
  }
  //#endregion GT 
}
