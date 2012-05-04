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
 * To git:
 * git clone https://MultiTool@github.com/MultiTool/GiverTaker.git
 */
public class Things {

  public Things() {
  }

  public enum OrgType {

    Giver, Taker
  };
  public static final int GiverType = 0, TakerType = 1, NumTypes = 2;
  public static Random rand = new Random();
  public static final Double LThresh = 0.5;
  public static final Double BThresh = LThresh + 0.5;

  public static class Org {

    public int MyType; // OrgType MyType;
    public double E;

    public double GetSpareE() {// food above survival level
      return this.E - LThresh;
    }

    public boolean IsMoribund() {
      return this.E < LThresh;// if E not enough to live
    }

    public Org CopyMe() {
      try {
        return (Org) this.clone();
      } catch (Exception ex) {
        return null;
      }
    }

    public Org GiveBirth() {
      return null;
    }

    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      g2.setColor(Color.BLACK);
      g2.fillRect(XOrg, YOrg, 16, 16);
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

    @Override
    public Org GiveBirth() {
      Giver child = null;
      try {
        child = new Giver();// (Giver) this.clone();
      } catch (Exception ex) {
        return null;
      }
      child.E = LThresh;/* Give the child LThresh, and bill the parent. */
      this.E -= child.E;
      return child;
    }

    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      g2.setColor(Color.green);
      g2.fillRect(XOrg, YOrg, 16, 16);
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

    @Override
    public Org GiveBirth() {
      Taker child = null;
      try {
        child = new Taker();// (Taker) this.clone();
      } catch (Exception ex) {
        return null;
      }
      child.E = LThresh;/* Give the child LThresh, and bill the parent. */
      this.E -= child.E;
      return child;
    }

    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      g2.setColor(Color.red);
      g2.fillRect(XOrg, YOrg, 16, 16);
    }
  };
  public static final Org[] OrgMaker = new Org[]{
    new Giver(), new Taker()
  };
  // private static final HashMap<Integer, OrgType> Num2Type = new HashMap<Integer, OrgType>();
  //private static final OrgType[] Num2Type = new OrgType[]{OrgType.Giver,OrgType.Taker};
  //private static final HashMap<OrgType,Integer > Type2Num = new HashMap<OrgType,Integer >();
  /* *************************************************************************************************** */

  public static class Soil {

    public Org Ctr;
    public int MyDex;
    public Soil FattestNbr;
    public double Fertility = 0.0;
    public final int NumNbrs = 8;
    public Soil[] Nbrs = new Soil[NumNbrs];
    /* *************************************************************************************************** */

    public Soil UpdateFertility() {
      Soil BestNbr = null;
      double BestNbrE = -1.0;
      int BestCnt = 0;
      // Pick the fattest neighbor. If there is a tie, pick randomly from among the best.
      for (int nbrcnt = 0; nbrcnt < NumNbrs; nbrcnt++) {
        Soil ph = Nbrs[nbrcnt];
        double sampleE;
        if (ph == null) {
          boolean nop = true;
        }
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
      this.Fertility = BestNbrE;// side effect! inelegant.
      this.FattestNbr = BestNbr;
      return BestNbr;
    }
    /* *************************************************************************************************** */

    public boolean IsFertile() {
      if (Fertility >= BThresh) {// wrong!! work this out right.
        return true;
      }
      return false;
    }
    /* *************************************************************************************************** */

    public void AcceptChild() {
      Org child = this.FattestNbr.Ctr.GiveBirth();// Birth
      this.Ctr = child;
    }
    /* *************************************************************************************************** */

    public void Vacate() {
      this.Ctr = null;// delete this.Ctr
      this.Fertility = 0;
    }
    /* *************************************************************************************************** */

    public double GetRegionE() {
      double NbrE = 0;
      for (int nbrcnt = 0; nbrcnt < NumNbrs; nbrcnt++) {
        Soil Nbr = Nbrs[nbrcnt];
        if (Nbr.Ctr != null) {
          NbrE += Nbr.Ctr.E;
        }
      }
      this.Fertility = NbrE;
      return NbrE;
    }
    /* *************************************************************************************************** */

    public double Spawn() {
      double[] NbrEList = new double[NumTypes];
      for (int nbrcnt = 0; nbrcnt < NumNbrs; nbrcnt++) {
        Soil PhNbr = Nbrs[nbrcnt];
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
    /* *************************************************************************************************** */

    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      //g2.setColor(Color.yellow);
      g2.setColor(Color.black);
      g2.drawRect(XOrg, YOrg, 20, 20);
      if (this.Ctr != null) {
        this.Ctr.Draw_Me(g2, XOrg, YOrg);
      } else {
        g2.setColor(Color.black);
        // g2.fillRect(XOrg + 2, YOrg + 2, 16, 16);
      }
    }
  }
  /* *************************************************************************************************** */

  public static class GridWorld extends ArrayList<Soil> {

    public int Sz;
    public int Wdt, Hgt;
    /* *************************************************************************************************** */

    public void Init_Topology(int WdtNew, int HgtNew) {
      this.Wdt = WdtNew;
      this.Hgt = HgtNew;
      Sz = HgtNew * WdtNew;

      // fill cells
      for (int cnt = 0; cnt < Sz; cnt++) {
        Soil ph = new Soil();
        ph.MyDex = cnt;
        this.add(ph);
      }

      // connect here
      for (int Row = 0; Row < Hgt; Row++) {
        for (int Col = 0; Col < Wdt; Col++) {
          Soil ctr = this.Get(Col, Row);// get self

          int localcnt = 0;
          for (int Ycnt = -1; Ycnt <= 1; Ycnt++) {
            int NbrY = Row + Ycnt;
            if (NbrY < 0) {
              NbrY += Hgt;
            } else if (NbrY >= Hgt) {
              NbrY -= Hgt;
            }

            for (int Xcnt = -1; Xcnt <= 1; Xcnt++) {
              if (!(Ycnt == 0 && Xcnt == 0)) {
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

    /* *************************************************************************************************** */
    public void Init_Seed() {
      double Birth_Weight = LThresh * 3;// + BThresh;
      for (int cnt = 0; cnt < this.Sz; cnt++) {
        Soil box = this.Get(cnt);
        if (rand.nextDouble() < 0.33) {
          box.Ctr = new Giver();
          box.Ctr.E = Birth_Weight;
        } else if (rand.nextDouble() < 0.66) {
          box.Ctr = new Taker();
          box.Ctr.E = Birth_Weight;
        }
      }
    }

    public Soil Get(int Col, int Row) {
      int Dex = Row * Wdt + Col;
      return this.get(Dex);
    }

    public Soil Get(int Dex) {
      return this.get(Dex);
    }

    public int GetSz() {
      return Sz;
    }
    /* *************************************************************************************************** */

    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      for (int Row = 0; Row < Hgt; Row++) {
        for (int Col = 0; Col < Wdt; Col++) {
          Soil cell = this.Get(Col, Row);
          cell.Draw_Me(g2, XOrg + Col * 20, YOrg + Row * 20);
        }
      }
    }
    /* *************************************************************************************************** */

    public void NacerMorir() {// To every thing, turn, turn
      GridWorld MyGrid = this;
      ArrayList<Soil> BirthList = new ArrayList<Soil>();
      ArrayList<Soil> DeathList = new ArrayList<Soil>();
      int Sz = MyGrid.GetSz();
      for (int cnt = 0; cnt < Sz; cnt++) {
        Soil ph = MyGrid.Get(cnt);
        if (ph.Ctr != null)// if my grid cell full:
        {
          if (ph.Ctr.IsMoribund()) {//if E not enough to live
            DeathList.add(ph);// mark for death
          }
        } else {//if this grid cell empty
          ph.UpdateFertility();// take E of region in grid
          if (ph.IsFertile()) {// mark cell for possible birth
            BirthList.add(ph);
          }
        }
      }

      Collections.shuffle(BirthList);// randomize to prevent spatial bias in birth order
      Collections.sort(BirthList, new Comparator() {// sort by sum E here.

        @Override
        public int compare(Object o1, Object o2) {
          Soil s1 = (Soil) o1;
          Soil s2 = (Soil) o2;
          return -Double.compare(s1.Fertility, s2.Fertility);// sort descending
        }
      });

      for (Soil ph : BirthList) {//now go down list, for each
        ph.UpdateFertility();// take E of region in grid
        if (ph.IsFertile())// if neighborhood still has resources to create child here
        {
          ph.AcceptChild();
        }
      }

      for (Soil ph : DeathList) {// Drag away the corpses.
        ph.Vacate();
      }
    }
  }
}
