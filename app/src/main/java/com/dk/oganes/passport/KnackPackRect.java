package com.dk.oganes.passport;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class KnackPackRect
{
  static private String  m_log = "KP2D";

  // ******************************************************
  // Internal types
  // ******************************************************

  class VecLineSegm
  {
    public int     xMin;
    public int     xMax;
    public int     y;
    public int     index;
  };

  class SegmComparatorByY implements Comparator<VecLineSegm>
  {
    @Override
    public int compare(VecLineSegm v1, VecLineSegm v2) {
      return v1.y - v2.y;
    }
  };

  private final int KP_ERROR_OK               = 0;
  private final int KP_ERROR_CANT_ADD         = -1;
  private final int KP_ERROR_WRONG_SEGMENTS   = -2;
  private final int KP_ERROR_SEG_CANT_SPLIT   = -3;
  private final int KP_ERROR_TOO_MANY_SEGS    = -4;
  private final int KP_ERROR_TOO_MANY_OBJECST = -5;

  private final int MAX_LINE_SEGMENTS  = 128;

  // ******************************************************
  // Data
  // ******************************************************
  private int           m_wPack;
  private int           m_hPack;
  private int           m_wObj;
  private int           m_hObj;

  // float horizont
  private ArrayList<VecLineSegm>    m_segments;
  private ArrayList<VecLineSegm>    m_segmentsSorted;
  private int                       m_numSegments;

  // objects added
  private int                       m_numObjects;
  private int                       m_maxObjects;
  private ObjDesc                   m_objects[];
  private ObjDesc                   m_objBest[];

  private int                       m_maxObjectsInAttempt;
  private int                       m_numAttempts = 1;
  private int                       m_attemptIndex = 0;


  // ******************************************************
  // Methods
  // ******************************************************
  KnackPackRect(int wPack, int hPack, int wObj, int hObj)
  {
    m_segments = new ArrayList<VecLineSegm>(MAX_LINE_SEGMENTS);
    m_segmentsSorted = new ArrayList<VecLineSegm>(MAX_LINE_SEGMENTS);

    // m_segments = new VecLineSegm[MAX_LINE_SEGMENTS];
    // m_segmentsSorted = new VecLineSegm[MAX_LINE_SEGMENTS];

    m_wPack = wPack; m_hPack = hPack;
    m_wObj = wObj; m_hObj = hObj;

    m_numSegments = 1;

    VecLineSegm seg;
    seg = new VecLineSegm();
    seg.xMin = 0;
    seg.xMax = wPack;
    seg.y = 0;
    seg.index = 0;
    m_segments.add(seg);

    seg = new VecLineSegm();
    seg.xMin = 0;
    seg.xMax = wPack;
    seg.y = 0;
    seg.index = 0;
    m_segmentsSorted.add(seg);

    int i;
    for (i = 1; i < MAX_LINE_SEGMENTS; i++)
    {
      seg = new VecLineSegm();
      seg.xMin = seg.xMax = 0;
      seg.y = 0;
      seg.index =  i;
      m_segments.add(seg);

      seg = new VecLineSegm();
      seg.xMin = seg.xMax = 0;
      seg.y = 0;
      seg.index =  i;
      m_segmentsSorted.add(seg);
    }
    // estimate max possbile objects in pack
    final int maxNumOnW = (int)(wPack / (wObj * 0.2f));
    final int maxNumOnH = (int)(hPack / (hObj * 0.2f));

    m_numObjects = 0;
    m_maxObjects = maxNumOnW * maxNumOnH;
    Log.d(m_log, "KnackPack constructor: max objects = " + String.valueOf(m_maxObjects));
    m_objects = new ObjDesc[m_maxObjects];
    m_objBest = new ObjDesc[m_maxObjects];
    for (i = 0; i < m_maxObjects; i++)
    {
      m_objects[i] = new ObjDesc();
      m_objBest[i] = new ObjDesc();;
    }
    m_maxObjectsInAttempt = 0;
    m_numAttempts = 1;
    m_attemptIndex = 0;
  }

  public int getCurAttempt()
  {
    return m_attemptIndex;
  }
  public int getNumAttempts()
  {
    return m_numAttempts;
  }

  public int getNumObjects()
  {
    return m_numObjects;
  }
  public ObjDesc[] getObjects()
  {
    return m_objects;
  }

  public ObjDesc[] getObjectsBest() {return m_objBest; }
  public int getNumObjectsBest()
  {
    return m_maxObjectsInAttempt;
  }

  private int isPossibleToAddObj(final int xLeft, final int yBot, final int w, final int h)
  {
    // check bound of pack
    final int dx = xLeft + w - m_wPack;
    final int dy = yBot + h - m_hPack;
    if (dx > 0)
      return 0;
    if (dy > 0)
      return 0;
    return 1;
  }

  private int splitSingleSegmentInTwo(final int iSegment, final int w, final int h)
  {
    if (m_numSegments + 1 >= MAX_LINE_SEGMENTS)
      return KP_ERROR_TOO_MANY_SEGS;
    int i;

    final int yOld = m_segments.get(iSegment).y;
    final int xMinOld = m_segments.get(iSegment).xMin;
    final int xMaxOld = m_segments.get(iSegment).xMax;

    // check is right collapsed
    final int wRight = (xMaxOld - xMinOld) - w;
    if (wRight <= 0)
    {
      // add to exist on top
      m_segments.get(iSegment).y = yOld + h;
      return 1;
    }

    // shift right segments after iSegment
    for (i = m_numSegments - 1; i > iSegment; i--)
    {
      m_segments.get(i + 1).xMin  = m_segments.get(i).xMin;
      m_segments.get(i + 1).xMax  = m_segments.get(i).xMax;
      m_segments.get(i + 1).y     = m_segments.get(i).y;
    } // for (i)

    // new left segment
    m_segments.get(iSegment).xMin = xMinOld;
    m_segments.get(iSegment).xMax = xMinOld + w;
    m_segments.get(iSegment).y    = yOld + h;

    // new right segment
    m_segments.get(iSegment + 1).xMin = xMinOld + w;
    m_segments.get(iSegment + 1).xMax = xMaxOld;
    m_segments.get(iSegment + 1).y = yOld;

    // add segment to counter
    m_numSegments++;
    return 1;
  }


  private int addObj(final int xLeft, final int yBot, final int w, final int h)
  {
    final int isPossibleToAdd = isPossibleToAddObj(xLeft, yBot, w, h);
    if (isPossibleToAdd == 0)
      return KP_ERROR_CANT_ADD;

    // find correspondonding segment
    V2d vDelta = new V2d();
    int iSegment = -1;
    for (int i = 0; i < m_numSegments; i++)
    {

      vDelta.x = xLeft - m_segments.get(i).xMin;
      vDelta.y = yBot- m_segments.get(i).y;
      if (vDelta.dotProduct(vDelta) < 2 * 2)
      {
        iSegment = i; break;
      }
    } // for (i)

    // wrong segments structure
    if (iSegment < 0)
      return KP_ERROR_WRONG_SEGMENTS;
    // check segment can be splitted
    final int dSeg = xLeft + w - m_segments.get(iSegment).xMax;
    if (dSeg > 0.001f)
      return KP_ERROR_SEG_CANT_SPLIT;
    int iRes = splitSingleSegmentInTwo(iSegment, w, h);

    // check is possible to store added object
    if (m_numObjects + 1 > m_maxObjects)
      return KP_ERROR_TOO_MANY_OBJECST;
    // add to object list
    m_objects[m_numObjects].xMin = xLeft;
    m_objects[m_numObjects].yMin = yBot;
    final int dw = (w - m_wObj);
    m_objects[m_numObjects].isRotated = (dw == 0) ? 0: 1;
    m_numObjects++;

    return iRes;
  }

  public int doPushStepWithOrientation(final int isNormOri)
  {
    int is;
    // setup segment indices
    for (is = 0; is < m_numSegments; is++)
    {
      m_segments.get(is).index = is;
    }
    // copy to sorted
    int i;
    for (i = 0; i < m_numSegments; i++)
    {
      m_segmentsSorted.get(i).index = m_segments.get(i).index;
      m_segmentsSorted.get(i).xMin = m_segments.get(i).xMin;
      m_segmentsSorted.get(i).xMax = m_segments.get(i).xMax;
      m_segmentsSorted.get(i).y = m_segments.get(i).y;
    }
    // sort segments in order of y increase (most lowest are first)
    SegmComparatorByY compareFunc = new SegmComparatorByY();
    Collections.sort(m_segmentsSorted, compareFunc);

    ArrayList<VecLineSegm> segms = m_segmentsSorted;
    int wObj = 0, hObj = 0;

    wObj = m_wObj;
    hObj = m_hObj;

    // setup random obj orientation
    //float frand = (float)Math.random() * 4096.0f;
    //if (((int)frand & 1) == 0)
    if (isNormOri == 0)
    {
      wObj = m_hObj;
      hObj = m_wObj;
    }

    int iSegToBeSplit = -1;
    for (is = 0; is < m_numSegments; is++)
    {

      final int wSeg = segms.get(is).xMax - segms.get(is).xMin;
      // check cant fit on width in segment
      if (wObj > wSeg)
        continue;
      // check can fit on pack width
      if (segms.get(is).xMin + wObj > m_wPack)
        continue;
      // check cant fit on height
      if (segms.get(is).y + hObj > m_hPack)
        continue;
      iSegToBeSplit = segms.get(is).index;
      break;

    } // for (is)
    if (iSegToBeSplit < 0)
      return 0;
    final int xToAdd = m_segments.get(iSegToBeSplit).xMin;
    final int yToAdd = m_segments.get(iSegToBeSplit).y;
    int res = addObj(xToAdd, yToAdd, wObj, hObj);
    return res;
  }

  public int doPackRandomStep()
  {
    float frand = (float)Math.random() * 4096.0f;
    final int isNormOri = (((int)frand & 1) == 0) ? 1: 0;
    int res = doPushStepWithOrientation(isNormOri);
    return res;
  } //

  public void startAttempts(final int numAttempts)
  {
    m_maxObjectsInAttempt = 0;
    m_numAttempts = numAttempts;
    m_attemptIndex = 0;
  }
  public void stopAttempts()
  {
    // restore best object config
    m_numObjects = m_maxObjectsInAttempt;
    int i;
    for (i = 0; i < m_numObjects; i++)
    {
      m_objects[i].xMin       = m_objBest[i].xMin;
      m_objects[i].yMin       = m_objBest[i].yMin;
      m_objects[i].isRotated  = m_objBest[i].isRotated;
    }
  }

  public int doAttempt()
  {
    if (m_attemptIndex >= m_numAttempts)
    {
      return 0;
    }
    m_attemptIndex++;

    m_numObjects = 0;
    // init horizont
    m_numSegments = 1;
    int i;
    for (i = 0; i < MAX_LINE_SEGMENTS; i++)
    {
      m_segments.get(i).xMin = 0;
      m_segments.get(i).xMax = (i == 0) ? m_wPack : 0;
      m_segments.get(i).index = i;
      m_segments.get(i).y = 0;
    } // for (i)

    for (i = 0; i < m_maxObjects; i++)
    {
      final int irand = (int)(Math.random() * 1024 * 8);
      // get bit (orientation)
      final int isNormOri = ((irand & 1) != 0) ? 1 : 0;
      final int wasAdded = doPushStepWithOrientation(isNormOri);
      if (wasAdded != 1)
        break;
    } // for (i) objs to push
    if (m_numObjects > m_maxObjectsInAttempt)
    {
      m_maxObjectsInAttempt = m_numObjects;
      // copy best config
      for (i = 0; i < m_numObjects; i++)
      {
        m_objBest[i].xMin = m_objects[i].xMin;
        m_objBest[i].yMin = m_objects[i].yMin;
        m_objBest[i].isRotated = m_objects[i].isRotated;
      }
    }
    return 1;
  }

} // class
