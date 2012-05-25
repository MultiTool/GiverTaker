/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package givertaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;

/**
 *
 * @author MultiTool
 * To git:
 * git clone https://MultiTool@github.com/MultiTool/GiverTaker.git
 */
public class Things {
  public static final int CellWdt = 6, CellHgt = 6;
  public static final int OrgBorder = 1;
  public static int OrgWdt = CellWdt - OrgBorder * 2, OrgHgt = CellHgt - OrgBorder * 2;
  public static double DramaFactor = 0.2;// scales size of transactions
  //public static double Entropy = 0.051 * DramaFactor;
  public static final double Entropy = 0.049 * DramaFactor;
  // None of these values have to be the same for both giver and taker, but for now they are for convenience.
  public static final Double Grace = 0.5;// to keep Takers from eating their children right away
  public static final Double LThresh = 1.0;
  public static final Double BThresh = LThresh * 2.0 + Grace * 2.0;
  public static final Double SelfImpact = 0.10;
  //public static final Double SocialImpact = 0.15;
  public static final Double SocialImpact = 0.20;
  public static Random rand = new Random();
  public static int Generation = 0;
  public Things() {
  }
  public enum OrgType {
    Giver, Taker
  };
  public static final int GiverType = 0, TakerType = 1, NumTypes = 2;
  /* *************************************************************************************************** */
  public static class Org {
    public int MyType; // OrgType MyType;
    public double E;
    public Org() {
      this.MyType = -1;// GiverType TakerType 
    }
    public double GetSpareE() {// food above survival level
      return this.E - LThresh;
    }
    public boolean IsMoribund() {
      return this.E < LThresh;// if E not enough to live
    }
    public void Interact(Org Nbr) {
    }
    public Org GiveBirth() {
      return null;
    }
    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      g2.setColor(Color.BLACK);
      g2.fillRect(XOrg + OrgBorder, YOrg + OrgBorder, OrgWdt, OrgHgt);
    }
  }
  /* *************************************************************************************************** */
  public static class Giver extends Org {
    static final double YouGetQuant = SocialImpact * DramaFactor;
    static final double IGiveQuant = SelfImpact * DramaFactor;
    public Giver() {
      this.MyType = GiverType;
    }
    @Override
    public double GetSpareE() {// food above survival level
      return this.E - LThresh;
    }
    @Override
    public void Interact(Org Nbr) {
      if (this.E > 0.0) {
        Nbr.E += YouGetQuant;
        this.E -= IGiveQuant;
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
      child.E = LThresh + Grace;/* Give the child LThresh, and bill the parent. */
      this.E -= child.E;
      return child;
    }
    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      g2.setColor(Color.green);
      g2.fillRect(XOrg + OrgBorder, YOrg + OrgBorder, OrgWdt, OrgHgt);
    }
  };
  /* *************************************************************************************************** */
  public static class Taker extends Org {
    static final double IGetQuant = SelfImpact * DramaFactor;
    static final double YouLoseQuant = SocialImpact * DramaFactor;
    public Taker() {
      this.MyType = TakerType;
    }
    @Override
    public double GetSpareE() {// food above survival level
      return this.E - LThresh;
    }
    @Override
    public void Interact(Org Nbr) {
      if (Nbr.E > 0.0) {
        Nbr.E -= YouLoseQuant;
        this.E += IGetQuant;
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
      child.E = LThresh + Grace;/* Give the child LThresh, and bill the parent. */
      this.E -= child.E;
      return child;
    }
    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      g2.setColor(Color.red);
      g2.fillRect(XOrg + OrgBorder, YOrg + OrgBorder, OrgWdt, OrgHgt);
    }
  };
  /* *************************************************************************************************** */
  public static final Org[] OrgMaker = new Org[]{
    new Giver(), new Taker()
  };
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
    public double Spawn() {// alternate way, all neighbors contribute to child
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
    public void Interact() {
      if (this.Ctr != null) {
        for (Soil Nbr : this.Nbrs) {
          if (Nbr.Ctr != null) {
            this.Ctr.Interact(Nbr.Ctr);
          }
        }
        this.Ctr.E -= Entropy;
      }
    }
    /* *************************************************************************************************** */
    public void Draw_Me(Graphics2D g2, int XOrg, int YOrg) {
      g2.setColor(Color.black);
      g2.drawRect(XOrg, YOrg, CellWdt, CellHgt);
      if (this.Ctr != null) {
        this.Ctr.Draw_Me(g2, XOrg, YOrg);
      }
    }
  }
  /* *************************************************************************************************** */
  public static class GridWorld extends ArrayList<Soil> {
    public int Sz;
    public int Wdt, Hgt;
    int BirthNum, DeathNum;// migrating from collections to arrays 
    Soil[] BirthList, DeathList; // in the future, want to migrate these to real arrays, for performance and C portability
    Soil[] ShuffleDex;
    int[] CensusRay = new int[]{0, 0};// NumTypes
    /* *************************************************************************************************** */
    public void Init() {
      this.Init_Topology(40, 40);
      if (true) {
        this.Init_Seed();
      } else {
        this.Init_Seed_Island();
      }
    }
    /* *************************************************************************************************** */
    public void Init_Topology(int WdtNew, int HgtNew) {
      this.Wdt = WdtNew;
      this.Hgt = HgtNew;
      Sz = HgtNew * WdtNew;
      BirthList = new Soil[Sz];
      DeathList = new Soil[Sz];
      ShuffleDex = new Soil[Sz];
      // fill cells
      for (int cnt = 0; cnt < Sz; cnt++) {
        Soil ph = new Soil();
        ph.MyDex = cnt;
        this.add(ph);
        ShuffleDex[cnt] = ph;
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
      double Birth_Weight = LThresh * 1.5;// + Grace * 1.5;
      for (int cnt = 0; cnt < this.Sz; cnt++) {
        Soil box = this.Get(cnt);
        double chance = rand.nextDouble();
        if (chance < 0.33) {
          box.Ctr = new Giver();
          box.Ctr.E = Birth_Weight;
        } else if (chance < 0.66) {
          box.Ctr = new Taker();
          box.Ctr.E = Birth_Weight;
        }
      }
    }
    /* *************************************************************************************************** */
    public void Init_Seed_Island() {
      double Birth_Weight = LThresh * 1.2;
      if (false) {
        for (int cnt = 0; cnt < this.Sz; cnt++) {
          Soil box = this.Get(cnt);
          double chance = rand.nextDouble();

          if (chance < 0.33) {
            box.Ctr = new Taker();
            box.Ctr.E = Birth_Weight;
          }
        }
      }
      // Init_Box_Colony(new Taker(), 17, 17, 4, 4); Init_Box_Colony(new Giver(), 18, 18, 5, 5);
      //Init_Box_Colony(new Taker(), 16, 16, 7, 7); Init_Box_Colony(new Giver(), 18, 18, 6, 6);
      Init_Box_Colony(new Giver(), 18, 18, 1, 2);
    }
    /* *************************************************************************************************** */
    public void Init_Box_Colony(Org First, int Xorg, int Yorg, int BoxWdt, int BoxHgt) {
      double Birth_Weight = LThresh * 1.2;
      int Left = Xorg;// (this.Wdt - BoxWdt) / 2;
      int Top = Yorg;// (this.Hgt - BoxHgt) / 2;
      int Right = (Left + BoxWdt);
      int Bottom = (Top + BoxHgt);
      for (int Ycnt = Top; Ycnt < Bottom; Ycnt++) {
        for (int Xcnt = Left; Xcnt < Right; Xcnt++) {
          Soil box = this.Get(Xcnt, Ycnt);
          box.Ctr = First.GiveBirth();
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
          cell.Draw_Me(g2, XOrg + Col * CellWdt, YOrg + Row * CellHgt);
        }
      }
      String Gen = Integer.toString(Generation);
      String GNum = Integer.toString(CensusRay[GiverType]);
      String TNum = Integer.toString(CensusRay[TakerType]);
      g2.setColor(Color.black);
      //g2.drawString(" G:" + GNum + " T:" + TNum, XOrg, YOrg + Hgt * CellHgt + 10);
      g2.drawString("Gen:" + Gen + " Givers:" + GNum + " Takers:" + TNum, XOrg, YOrg);
    }
    /* *************************************************************************************************** */
    public void Run_Cycle() {
      this.Interact();
      this.NacerMorir();
      Census();
      if (CensusRay[GiverType] != 0) {
        if (CensusRay[TakerType] != 0) {
          Generation++;
        }
      }
    }
    /* *************************************************************************************************** */
    public void Interact() {
      ShuffleCells(ShuffleDex, this.Sz);// shuffle to prevent spatial bias in order
      for (int cnt = 0; cnt < this.Sz; cnt++) {
        Soil sl = this.ShuffleDex[cnt];
        sl.Interact();
      }
    }
    /* *************************************************************************************************** */
    public void NacerMorir() {// To every thing, turn, turn
      GridWorld MyGrid = this;
      BirthNum = 0;
      DeathNum = 0;
      // BirthList.clear(); DeathList.clear();
      int Sz = MyGrid.GetSz();
      for (int cnt = 0; cnt < Sz; cnt++) {
        Soil ph = MyGrid.Get(cnt);
        if (ph.Ctr != null)// if my grid cell full:
        {
          if (ph.Ctr.IsMoribund()) {//if E not enough to live
            DeathList[DeathNum] = ph;//DeathList.add(ph);// mark for death
            DeathNum++;
          }
        } else {//if this grid cell empty
          ph.UpdateFertility();// take E of region in grid
          if (ph.IsFertile()) {// mark cell for possible birth
            BirthList[BirthNum] = ph;//BirthList.add(ph);
            BirthNum++;
          }
        }
      }
      ShuffleCells(BirthList, BirthNum);// randomize to prevent spatial bias in birth order
      SortCells(BirthList, BirthNum);// sort by sum E here.
      /*
       Collections.shuffle(Arrays.asList(BirthList));// randomize to prevent spatial bias in birth order
       Collections.sort(Arrays.asList(BirthList), new Comparator() {// sort by sum E here.
       @Override
       public int compare(Object o1, Object o2) {
       Soil s1 = (Soil) o1;
       Soil s2 = (Soil) o2;
       return Double.compare(s2.Fertility, s1.Fertility);// sort descending
       }
       });
       */
      for (int cnt = 0; cnt < BirthNum; cnt++) {
        Soil ph = BirthList[cnt];// go from most fertile to least fertile soil
        ph.UpdateFertility();// take E of region in grid
        if (ph.IsFertile())// if neighborhood still has resources to create child here
        {
          ph.AcceptChild();
        }
      }

      for (int cnt = 0; cnt < DeathNum; cnt++) {// Drag away the corpses.
        Soil ph = DeathList[cnt];// Drag away the corpses.
        ph.Vacate();
      }
    }
    /* *************************************************************************************************** */
    public void Census() {
      CensusRay = new int[]{0, 0};// NumTypes
      for (int cnt = 0; cnt < this.Sz; cnt++) {
        Soil sl = this.Get(cnt);
        if (sl.Ctr != null) {
          CensusRay[sl.Ctr.MyType]++;
        }
      }
    }
  }
  /* *************************************************************************************************** */
  public static void SortCells(Soil[] Cells, int Size) {
    Arrays.sort(Cells, 0, Size, new Comparator() {// sort by sum E here.
      @Override
      public int compare(Object o1, Object o2) {
        Soil s1 = (Soil) o1;
        Soil s2 = (Soil) o2;
        return Double.compare(s2.Fertility, s1.Fertility);// sort descending
      }
    });
  }
  /* *************************************************************************************************** */
  public static void ShuffleCells(Soil[] Cells, int Size) {
    Soil Temp;// randomize to prevent spatial bias in birth order      
    int Odex;
    for (int cnt = 0; cnt < Size; cnt++) {
      Odex = rand.nextInt(Size);
      Temp = Cells[cnt];
      Cells[cnt] = Cells[Odex];
      Cells[Odex] = Temp;
    }
  }
}
