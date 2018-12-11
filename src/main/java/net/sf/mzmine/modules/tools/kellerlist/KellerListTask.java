package net.sf.mzmine.modules.tools.kellerlist;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.PolarityType;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

public class KellerListTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private ParameterSet parameters;
  private DecimalFormat numberFormat = new DecimalFormat("0.0000");
  private RawDataFile dataFile;
  private MZTolerance mzTolerance;
  private ScanSelection scanSelection;
  private Scan scans[];
  private boolean hasNegativePolarity = false;
  private boolean hasPositivePolarity = false;
  private double noiseLevel;

  // scan counter
  private int processedSteps = 0, totalSteps;

  // Data for table
  private double[] monoIsoMassesPos = new double[] {33.03349, 42.03383, 59.06037, 63.04406,
      64.01577, 65.05971, 74.06004, 74.06004, 77.05971, 79.02121, 83.06037, 85.02600, 85.05887,
      88.03931, 96.04198, 99.04165, 100.07569, 100.99994, 101.00316, 101.08084, 102.05496,
      102.12773, 103.95560, 104.99229, 105.04232, 105.95379, 107.07027, 115.01559, 115.08659,
      120.04776, 122.08117, 123.06278, 123.09167, 124.03690, 129.05222, 130.15903, 132.90490,
      133.10705, 135.10157, 137.07431, 142.02971, 144.17468, 144.98215, 145.02615, 146.06887,
      146.98034, 147.11280, 149.02332, 150.12773, 151.09649, 153.13862, 155.08900, 157.03515,
      157.08352, 158.96403, 163.03897, 163.13287, 169.09475, 169.11046, 171.00527, 172.03931,
      173.05745, 173.07843, 179.01709, 181.12231, 183.08044, 183.14383, 185.11482, 186.22163,
      189.05237, 190.04987, 193.14344, 195.06519, 195.12270, 203.10425, 205.12578, 212.03181,
      214.08963, 215.12538, 217.10465, 221.18999, 225.19614, 228.00575, 231.09932, 231.11618,
      233.07858, 236.07157, 239.14892, 239.22485, 241.22190, 242.28423, 243.11683, 243.17194,
      251.18530, 251.20056, 257.03103, 261.13086, 265.21621, 267.17197, 273.12739, 273.16725,
      273.18250, 277.10480, 279.09333, 279.15909, 279.22945, 282.27914, 283.17513, 284.29479,
      287.19815, 288.25332, 289.14118, 293.24510, 295.22677, 301.14103, 304.26108, 305.15708,
      306.27673, 309.22717, 309.24242, 315.25299, 317.11497, 317.20872, 321.13101, 323.25567,
      325.25847, 327.07807, 327.20135, 331.20911, 331.22437, 337.11841, 337.27132, 338.34174,
      339.25299, 347.18305, 349.18329, 353.26864, 355.06994, 355.36829, 360.32368, 361.23493,
      365.15723, 367.26903, 367.28188, 368.42508, 371.10124, 371.22756, 371.31559, 371.31559,
      375.25058, 379.09246, 381.29753, 383.27920, 388.12779, 389.25098, 391.28429, 393.20951,
      397.29485, 405.22491, 405.26115, 409.18344, 411.30810, 413.26623, 415.25378, 419.27680,
      425.31090, 425.32375, 427.30542, 429.08873, 429.24017, 437.23572, 441.01479, 441.32107,
      443.01298, 445.12003, 447.29284, 449.28736, 449.38500, 453.20966, 453.34353, 454.29278,
      455.33431, 459.27999, 462.14658, 463.26678, 463.30301, 469.34996, 471.33163, 472.28781,
      481.26194, 483.35276, 485.34728, 493.31358, 494.56593, 497.23587, 499.36053, 503.10752,
      503.30621, 505.33471, 507.32923, 513.37618, 515.33001, 515.35785, 515.41286, 519.13882,
      521.30864, 522.59723, 525.28815, 529.37350, 531.40777, 531.47717, 536.16537, 537.33979,
      537.87901, 541.26209, 541.39463, 543.38674, 547.33242, 547.40269, 550.62853, 551.35544,
      553.38972, 553.45912, 555.88957, 557.40239, 559.38406, 563.37657, 568.13506, 569.31437,
      571.35622, 573.39971, 577.12631, 579.35051, 581.36601, 585.28830, 587.41296, 591.35864,
      593.15761, 595.38166, 597.90014, 599.43649, 601.42861, 603.41028, 606.09149, 610.18416,
      613.34058, 615.40375, 617.42593, 621.41844, 621.97291, 625.39222, 629.31452, 631.43917,
      633.32023, 635.38485, 637.39237, 639.40787, 645.45482, 646.35187, 647.43649, 651.14510,
      657.36680, 657.47836, 659.38350, 661.45214, 667.17640, 669.41844, 672.40390, 672.40390,
      673.34073, 675.46539, 679.41107, 679.46030, 679.51166, 683.43409, 684.20295, 689.48104,
      691.46271, 695.43424, 701.39301, 704.38250, 705.47836, 713.44465, 715.52022, 717.36695,
      719.49160, 723.43728, 725.16390, 727.46030, 732.46544, 733.50725, 735.48892, 737.50217,
      741.19520, 742.44979, 743.44101, 745.41923, 749.50457, 753.47610, 757.47087, 758.22175,
      758.41553, 761.39316, 763.51782, 767.46350, 771.48652, 773.56209, 777.53347, 779.51514,
      789.44544, 793.53079, 795.54403, 798.58785, 801.49708, 802.43051, 803.54324, 804.40978,
      805.41626, 805.41938, 807.39954, 807.54403, 809.44035, 809.48691, 811.48971, 811.51797,
      815.51273, 819.51718, 821.55968, 823.54135, 824.49887, 827.42978, 827.46214, 831.60395,
      832.48870, 833.47166, 837.55700, 839.09742, 842.50943, 845.10543, 845.52330, 848.49886,
      849.44559, 851.57025, 853.51313, 853.58590, 855.07136, 855.51593, 859.53895, 861.07937,
      863.47338, 865.47780, 865.54951, 865.58590, 867.08737, 867.56757, 869.55983, 870.54073,
      871.04530, 871.49959, 874.49926, 877.49787, 881.47271, 881.58322, 883.51485, 889.54951,
      889.64582, 891.56516, 893.47181, 893.58081, 895.59646, 897.53934, 899.53089, 899.54214,
      903.56516, 905.67979, 906.50434, 906.50434, 909.57573, 909.61211, 911.59378, 911.62776,
      917.49920, 921.52409, 925.60943, 927.60170, 931.51485, 933.57573, 935.59138, 937.49802,
      937.60703, 939.62268, 941.56556, 947.59138, 947.68768, 950.47305, 953.60194, 953.63833,
      955.62000, 969.63565, 969.66963, 973.53129, 977.60194, 979.50949, 979.61759, 981.63324,
      983.64889, 985.59177, 985.64356, 991.61759, 994.15551, 995.51966, 997.62816, 997.66454,
      999.64621, 1000.56734, 1002.58299, 1003.55309, 1005.72955, 1013.66186, 1020.50302, 1020.53604,
      1021.62816, 1023.47417, 1023.51055, 1023.64381, 1025.65946, 1027.67511, 1027.71149,
      1029.61799, 1031.59830, 1033.51603, 1035.64381, 1036.08277, 1036.53229, 1037.52620,
      1041.65437, 1041.69076, 1043.67243, 1043.68543, 1044.11395, 1045.56365, 1046.59528,
      1054.09334, 1057.68808, 1060.08789, 1060.56331, 1063.77141, 1064.57349, 1064.60987,
      1065.49934, 1065.65437, 1066.09590, 1066.60439, 1067.67002, 1069.68567, 1071.60445,
      1071.70132, 1073.64420, 1079.67002, 1080.53201, 1082.59931, 1085.68059, 1085.71697,
      1085.75336, 1087.69864, 1090.53097, 1091.54800, 1101.71429, 1101.72729, 1109.48982,
      1109.68059, 1111.56048, 1111.69624, 1113.71189, 1115.72754, 1117.67042, 1118.50860,
      1121.58372, 1121.81328, 1123.69624, 1126.56399, 1129.62117, 1129.70680, 1129.74319,
      1141.51941, 1143.55031, 1143.79522, 1153.57354, 1155.72245, 1157.59093, 1157.73810,
      1159.76916, 1161.69663, 1165.58478, 1168.58194, 1173.73302, 1175.52238, 1175.63404,
      1179.60043, 1179.85514, 1183.61574, 1190.60855, 1191.51730, 1191.62896, 1193.61608,
      1199.74867, 1200.57177, 1201.76432, 1201.83709, 1203.64019, 1205.72285, 1208.61574,
      1210.59501, 1210.59501, 1217.75923, 1217.81102, 1219.63510, 1220.59059, 1225.12537,
      1229.80022, 1232.54385, 1232.59780, 1235.52873, 1243.77488, 1245.79053, 1248.53876,
      1249.74906, 1251.70959, 1254.60730, 1261.78545, 1262.59714, 1262.75793, 1265.63721,
      1267.70450, 1277.63319, 1277.70998, 1278.58553, 1278.64369, 1282.66912, 1287.80110,
      1289.81675, 1293.77528, 1296.68477, 1301.65834, 1302.70121, 1302.71512, 1305.71613,
      1305.81166, 1307.67764, 1308.64302, 1308.65426, 1315.68523, 1316.72425, 1320.58283,
      1323.67255, 1329.63933, 1331.82731, 1332.71917, 1333.84296, 1336.71071, 1337.80149,
      1340.66924, 1344.67539, 1349.83788, 1350.68729, 1357.69579, 1357.71825, 1365.63933,
      1366.69612, 1367.67024, 1371.71144, 1374.69334, 1375.85353, 1377.86918, 1381.64817,
      1381.82771, 1383.69031, 1384.72999, 1390.68087, 1393.73217, 1393.86409, 1399.69262,
      1399.70048, 1418.72742, 1419.74783, 1419.79544, 1419.87974, 1421.89539, 1424.76448,
      1425.85392, 1433.72056, 1434.76996, 1437.89031, 1438.74374, 1439.81176, 1442.80019,
      1443.64204, 1447.77510, 1449.71548, 1453.83731, 1463.90596, 1465.71039, 1465.92161,
      1469.88014, 1475.74888, 1475.78527, 1476.80567, 1479.79544, 1481.91652, 1490.74203,
      1491.74380, 1491.75185, 1493.59953, 1493.73430, 1495.61518, 1507.73872, 1507.93217,
      1509.94782, 1513.90635, 1521.75436, 1522.73186, 1525.94274, 1549.67720, 1551.95839,
      1553.97404, 1557.93257, 1566.73944, 1567.74274, 1569.96895, 1585.75840, 1595.98460,
      1598.00025, 1599.83368, 1601.95878, 1613.99517, 1623.88532, 1638.76566, 1640.01082,
      1639.93775, 1642.02647, 1645.98500, 1657.79287, 1658.02138, 1665.90443, 1676.77623,
      1684.03703, 1686.05268, 1687.97011, 1690.01121, 1702.04760, 1707.77214, 1708.71325,
      1713.80785, 1716.85112, 1725.86287, 1728.06325, 1730.07890, 1734.03743, 1744.92283,
      1746.07381, 1757.85270, 1765.73471, 1772.08946, 1774.10511, 1774.85026, 1774.89700,
      1778.06364, 1790.10003, 1791.72772, 1792.87858, 1797.01165, 1816.11568, 1818.13133,
      1830.84056, 1834.12624, 1837.96542, 1838.91439, 1847.80510, 1851.92693, 1860.14189,
      1862.15754, 1940.93485, 1987.07801, 1993.97666, 1993.98253, 2003.07293, 2021.98868,
      2055.01485, 2082.96483, 2083.00908, 2109.01285, 2150.07777, 2155.08522, 2163.05642,
      2171.02466, 2184.12551, 2185.03837, 2193.00248, 2193.99436, 2201.01231, 2208.99740,
      2209.98927, 2211.10404, 2225.11969, 2225.15608, 2239.13534, 2240.16966, 2251.01582,
      2267.01074, 2273.15944, 2283.18018, 2283.18018, 2286.12484, 2289.15437, 2295.14139,
      2297.19584, 2305.14928, 2311.13631, 2321.20707, 2329.17425, 2343.18901, 2353.19690,
      2367.26267, 2375.17884, 2383.95186, 2400.27441, 2501.06722, 2501.25182, 2510.13177,
      2514.33847, 2530.33339, 2550.23269, 2552.24834, 2564.18093, 2564.36786, 2566.27838,
      2581.19473, 2612.18093, 2613.34938, 2614.19658, 2666.29127, 2670.37084, 2705.16112,
      2707.41621, 2720.36337, 2728.23951, 2746.41925, 2748.39467, 2807.31407, 2872.39278,
      2902.41052, 2904.38261, 2914.50576, 2932.51632, 3211.47446, 3223.28159, 3227.46937,
      3312.30814, 3337.73214, 3346.68149, 3353.72706};

  private double[] monoIsoMassesNeg = new double[] {26.00362, 44.99820, 59.01385, 78.95906,
      79.95737, 94.98084, 96.96011, 96.96962, 112.98559, 112.98563, 126.90502, 162.98240, 212.07507,
      226.97845, 248.96040, 255.23295, 281.24860, 283.26425, 316.73910, 318.73729};


  private String[] ionTypePos = new String[] {"[M+H]+", "[M+H]+", "[M+NH4]+", "[A1B+H]+", "[M+Na]+",
      "[M2+H]+", "[M+H]+", "[A1B1+H]+", "[A1B+H]+", "[M+H]+", "[M2+H]+", "[A1B+Na]+", "[M+H]+",
      "[A1B1+H]+", "[A1B1+Na]+", "[A1B+Na]+", "[M+H]+", "[A1B+K]+", "[M+Na]+", "[A2B2+H]+",
      "[A1B1+H]+", "[M+H]+", "[M+63Cu]+", "[M+Na]+", "[M2+Na]+", "[M+65Cu]+", "[A2B+H]+",
      "[A1B+K]+", "[A1B1+H]+", "[M+CH3CN+H]+", "[M+H]+", "[A2B2+Na]+", "[M+H]+", "[A1B1+Na]+",
      "[A2B+Na]+", "[M+H]+", "M+", "[A3B2+H]+", "[A2B+H]+", "[M+CH3CN+NH4]+", "[M+CH3CN+Na]+",
      "[M+H]+", "[M2+63Cu]+", "[A2B+K]+", "[M3+Na]+", "[M2+65Cu]+", "[A2B2+H]+", "[f+H]+", "[M+H]+",
      "[A3B+H]+", "[M+H]+", "[A3B2+Na]+", "[M2+H]+", "[A2B+Na]+", "[M+Na]+", "[M-CH3OH+H]+",
      "[M+H]+", "[A2B2+Na]+", "[M2+H]+", "[f+Na]+", "[M-H2O+H]+", "[A2B+K]+", "[A3B+Na]+",
      "[M2+Na]+", "[M+H]+", "[M+H]+", "[A4B3+H]+", "[M+Na]+", "[M+H]+", "[A3B+K]+", "[M+H]+",
      "[A3B+H]+", "[M+H]+", "[A4B+H]+", "[M+Na]+", "[A4B3+Na]+", "[M+Na]+", "[M+H]+", "[A3B+Na]+",
      "[A4B+Na]+", "[M+H]+", "[M+H]+", "[M+K]+", "[A3B+K]+", "[M+NH4]+", "[A4B+K]+", "[M+Na]+",
      "[A5B+H]+", "[(M.H35Cl)2-Cl]+", "[(M.H37Cl)2-Cl]+", "M+", "M+", "[M+Na]+", "[A4B+H]+",
      "[AB1+H]+", "[M3+Na]+", "[A5B+Na]+", "[AB1+H]+", "[M+H]+", "M+", "[A4B+Na]+", "[AB1+Na]+",
      "[A5B+K]+", "[M+H]+", "[M+H]+", "[AB1+Na]+", "[M+H]+", "[A6B+H]+", "[M+H]+", "[AB1+Na]+",
      "[M+H]+", "[A4B+K]+", "[AB1+Na]+", "[AB2+H]+", "[M+Na]+", "[M+Na]+", "[A6B+Na]+", "[M+Na]+",
      "[A5B+H]+", "[AB2+H]+", "[M+H]+", "[M+K]+", "[AB2+Na]+", "[A6B+K]+", "[AB2+Na]+", "[M2+H]+",
      "[M+H]+", "[A7B+H]+", "[A5B+Na]+", "[AB2+Na]+", "[M+H]+", "(120Sn)", "[AB2+Na]+", "[M+H]+",
      "[AB3+H]+", "[A5B+K]+", "[A7B+Na]+", "[AB3+H]+", "[M+H-CH4]+", "[M-Cl]+", "[M+Na]+",
      "[AB3+Na]+", "[A7B+K]+", "[A6B+H]+", "[AB3+Na]+", "[M-Cl]+", "[M+H]+", "[A8B+H]+", "[M+H]+",
      "[M+H]+", "[AB3+Na]+", "[M2+H]+", "[AB3+Na]+", "[AB4+H]+", "[M+NH4]+", "[A6B+Na]+", "[M+H]+",
      "[A8B+Na]+", "[AB4+H]+", "[A6B+K]+", "[AB4+Na]+", "[A8B+K]+", "[AB4+Na]+", "[M+Na]+",
      "[A9B+H]+", "[AB4+Na]+", "[A7B+H]+", "[AB4+Na]+", "[AB5+H]+", "[M+H-CH4]+", "[M+K]+",
      "[A9B+Na]+", "[M3+63Cu(I)]+", "[AB5+H]+", "[M3+65Cu(I)]+", "[M+H]+", "[M+H]+", "[AB5+Na]+",
      "[M2+H]+", "[A9B+K]+", "[M+H]+", "[M+CH3CN+Na]+", "[AB5+Na]+", "[A10B+H]+", "[M+NH4]+",
      "[A7B+K]+", "[AB5+Na]+", "[AB5+Na]+", "[AB6+H]+", "[M+H]+", "[A10B+Na]+", "[A8B+H]+",
      "[AB6+H]+", "[AB6+Na]+", "[M-Cl]+", "[A10B+K]+", "[AB6+Na]+", "[M+H-CH4]+", "[A11B+H]+",
      "[A8B+Na]+", "[AB6+Na]+", "[AB6+Na]+", "[M+H]+", "[AB7+H]+", "[M+H]+", "[M+H]+", "[A8B+K]+",
      "[M-Cl]+", "[A11B+Na]+", "[AB7+H]+", "[M+H]+", "[M+H]+", "[M+NH4]+", "[AB7+Na]+",
      "[M6-6H+3Fe+O]+", "[A11B+K]+", "[A9B+H]+", "[AB7+Na]+", "[A12B+H]+", "[M+H]+", "[M-Cl]+",
      "[AB7+Na]+", "[M+Na]+", "[M+Na]+", "[M6-6H+H2O+3Fe+O]+", "[AB7+Na]+", "[AB8+H]+", "[A9B+Na]+",
      "[M3+H]+", "[A12B+Na]+", "[M+H]+", "[AB8+H]+", "[M+H-CH4]+", "[A9B+K]+", "[AB8+Na]+",
      "[A12B+K]+", "[AB8+Na]+", "[A13B+H]+", "[M+H]+", "[AB8+Na]+", "[M7-6H+3Fe+O]+", "[A10B+H]+",
      "[AB8+Na]+", "[AB9+H]+", "[M3+K]+", "[M+NH4]+", "[A13B+Na]+", "[M+H]+", "[AB9+H]+",
      "[A10B+Na]+", "[M6-6H+3Fe+O]+", "[AB9+Na]+", "[A13B+K]+", "[AB9+Na]+", "[M+H]+", "[A14B+H]+",
      "[A10B+K]+", "[AB9+Na]+", "[AB9+Na]+", "[f+H]+", "[AB10+H]+", "[M+H-CH4]+", "[A14B+Na]+",
      "[A11B+H]+", "[M+H]+", "[AB10+H]+", "[M+H]+", "[AB10+Na]+", "[M+H]+", "[M+H]+", "[A14B+K]+",
      "[AB10+Na]+", "[A15B+H]+", "[A11B+Na]+", "[M+H]+", "[AB10+Na]+", "[M+NH4]+", "[AB10+Na]+",
      "[AB11+H]+", "[A11B+K]+", "[A15B+Na]+", "[M+H]+", "[AB11+H]+", "[AB11+Na]+", "[A12B+H]+",
      "[A15B+K]+", "[AB11+Na]+", "[A16B+H]+", "[M+H-CH4]+", "[AB11+Na]+", "[M+H]+", "[AB11+Na]+",
      "[AB12+H]+", "[A12B+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[A16B+Na]+", "[AB12+H]+",
      "[A12B+K]+", "[AB12+Na]+", "[M+NH4]+", "[M+H]+", "[A16B+K]+", "[AB12+Na]+", "[A17B+H]+",
      "[AB12+Na]+", "[A13B+H]+", "[AB12+Na]+", "[AB13+H]+", "[A17B+Na]+", "[AB13+H]+", "[A13B+Na]+",
      "[M2+NH4]+", "[AB13+Na]+", "[M+H]+", "[M2+Na]+", "[M+H]+", "[M+H]+", "[A17B+K]+", "[M+H]+",
      "[AB13+Na]+", "[M+H]+", "[AB10+Na]+", "[A18B+H]+", "[A13B+K]+", "[AB13+Na]+", "[M2+K]+",
      "[AB13+Na]+", "[AB14+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[A14B+H]+", "[M+H]+", "[A18B+Na]+",
      "[AB14+H]+", "[M4-2H+K+2Na]+", "[M+H]+", "[M4-3H+4Na]+", "[AB14+Na]+", "[M+H]+", "[A18B+K]+",
      "[AB14+Na]+", "[AB11+Na]+", "[A14B+Na]+", "[M4-2H+Na+2K]+", "[A19B+H]+", "[AB14+Na]+",
      "[M4-3H+3Na+K]+", "[f+H]+", "[f+H]+", "[AB10+Na]+", "[AB14+Na]+", "[M4-4H+5Na]+", "[AB15+H]+",
      "[A14B+K]+", "[M+H]+", "[M4-2H+3K]+", "[M+H]+", "[M+H]+", "[A19B+Na]+", "[M+H]+", "[AB15+H]+",
      "[M+H]+", "[AB15+Na]+", "[A15B+H]+", "[AB10+Na]+", "[A19B+K]+", "[AB10+Na]+", "[AB15+Na]+",
      "[AB12+Na]+", "[M+H]+", "[A20B+H]+", "[AB15+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB11+Na]+",
      "[AB15+Na]+", "[AB16+H]+", "[A15B+Na]+", "[M+H]+", "[A20B+Na]+", "[AB16+H]+", "[A15B+K]+",
      "[M+H]+", "[AB16+Na]+", "[AB11+Na]+", "[A20B+K]+", "[AB11+Na]+", "[AB16+Na]+", "[AB13+Na]+",
      "[AB16+Na]+", "[A16B+H]+", "[M+H]+", "[AB12+Na]+", "[AB16+Na]+", "[AB17+H]+", "[AB17+H]+",
      "[A16B+Na]+", "[M+H]+", "[AB17+Na]+", "[M+H]+", "[AB12+Na]+", "[AB12+Na]+", "[AB17+Na]+",
      "[AB14+Na]+", "[A16B+K]+", "[AB17+Na]+", "[M5-H2O-2H+3Na]+", "[M+H]+", "[AB13+Na]+",
      "[AB17+Na]+", "[AB18+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[A17B+H]+", "[AB18+H]+", "[M+H]+",
      "[M+H]+", "[AB18+Na]+", "[M+H]+", "[M+H]+", "[AB13+Na]+", "[AB13+Na]+", "[AB18+Na]+",
      "[A17B+Na]+", "[AB15+Na]+", "[M+H]+", "[M+H]+", "[AB18+Na]+", "[M5-2H2O-4H+4Na+K]+", "[M+H]+",
      "[M+H]+", "[AB14+Na]+", "[AB18+Na]+", "[AB19+H]+", "[A17B+K]+", "[M5-2H+Na+2K]+", "[M+H]+",
      "[M+H]+", "[M5-H2O-4H+4Na+K]+", "[AB19+H]+", "[M5-2H+3K]+", "[M+H]+", "[A18B+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[AB19+Na]+", "[M5-3H+2K+2Na]+", "[M+H]+", "[AB14+Na]+", "[AB14+Na]+",
      "[M+H]+", "[AB19+Na]+", "[AB16+Na]+", "[AB19+Na]+", "[f+H]+", "[M+H]+", "[AB15+Na]+",
      "[AB19+Na]+", "[A18B+Na]+", "[AB20+H]+", "[M+H]+", "[M+H]+", "[AB20+H]+", "[A18B+K]+",
      "[M+H]+", "[AB20+Na]+", "[M+H]+", "[AB15+Na]+", "[AB15+Na]+", "[AB20+Na]+", "[AB17+Na]+",
      "[M+H]+", "[M+H]+", "[A19B+H]+", "[AB20+Na]+", "[M+H]+", "[M+H]+", "[AB16+Na]+", "[AB20+Na]+",
      "[M+H]+", "[M+H]+", "[A19B+Na]+", "[M+H]+", "[AB16+Na]+", "[M+H]+", "[AB16+Na]+", "[A19B+K]+",
      "[AB18+Na]+", "[M+H]+", "[M+H]+", "[AB17+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[A20B+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB17+Na]+", "[M+H]+", "[AB17+Na]+",
      "[A20B+Na]+", "[M+H]+", "[AB19+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB18+Na]+", "[A20B+K]+",
      "[M+H]+", "[M+H]+", "[M6-2H2O-4H+4Na+K]+", "[M2+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[AB18+Na]+", "[AB18+Na]+", "[M+H]+", "[AB20+Na]+", "[M+H]+", "[M+H]+", "[AB19+Na]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[AB19+Na]+", "[AB19+Na]+", "[AB21+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[AB20+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[AB20+Na]+", "[M+H]+", "[AB20+Na]+", "[M+H]+", "[AB22+Na]+", "[M+H]+", "[M+H]+",
      "[AB21+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[AB21+Na]+", "[AB21+Na]+", "[M+H]+", "[AB23+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[AB22+Na]+", "[M+H]+", "[f+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB22+Na]+", "[AB22+Na]+",
      "[M+H]+", "[AB24+Na]+", "[M+H]+", "[M+H]+", "[AB23+Na]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB23+Na]+", "[M+H]+", "[AB23+Na]+", "[AB25+Na]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB24+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[AB24+Na]+", "[AB24+Na]+", "[AB26+Na]+", "[M+H]+", "[M+H]+",
      "[AB25+Na]+", "[M+H]+", "[AB25+Na]+", "[AB25+Na]+", "[AB27+Na]+", "[M+H]+", "[M+H]+",
      "[AB26+Na]+", "[M+H]+", "[AB26+Na]+", "[AB26+Na]+", "[M+H]+", "[AB28+Na]+", "[AB27+Na]+",
      "[M+H]+", "[M+H]+", "[AB27+Na]+", "[M+H]+", "[AB27+Na]+", "[AB29+Na]+", "[M+H]+",
      "[AB28+Na]+", "[M+H]+", "[M+H]+", "[AB28+Na]+", "[AB28+Na]+", "[M+H]+", "[AB30+Na]+",
      "[AB29+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB29+Na]+", "[AB29+Na]+",
      "[AB31+Na]+", "[M+H]+", "[AB30+Na]+", "[M+H]+", "[M+H]+", "[AB30+Na]+", "[AB30+Na]+",
      "[M+H]+", "[M+H]+", "[AB32+Na]+", "[AB31+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB31+Na]+",
      "[AB31+Na]+", "[M+H]+", "[AB32+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[AB32+Na]+",
      "[AB32+Na]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+Na]+", "[M+H]+",
      "[M+K]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[f+H]+", "[M+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+Na]+", "[M+H]+",
      "[M+H]+", "[M+Na]+", "[M+H]+", "[M+H]+", "[M+Na]+", "[M+H]+", "[M+H]+", "[M+Na]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+",
      "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+", "[M+H]+"};

  private String[] ionTypeNeg = new String[] {"f-", "[M-H]-", "[M-H]-", "[M-H3O]-", "[M-H2O]-",
      "[M-H]-", "[M-H]-", "[M-H]-", "[M-H]-", "[M2+Na-2H]-", "A-", "[M-H]-", "[M-H]-", "[M2-H]-",
      "[M2+Na-2H]-", "[M-H]-", "[M-H]-", "[M-H]-", "[63CuI+I]-", "[65CuI+I]-"};


  private String[] formulaPos = new String[] {"CH3OH", "CH3CN", "CH3CN", "[C2H4O]nH2O", "CH3CN",
      "CH3OH", "C3H7NO", "(CH3CN)n(CH3OH)m", "[C3H6O]nH2O", "C2H6OS", "CH3CN", "[C2H4O]nH2O",
      "C2D6OS", "(CH3CN)n(HCOOH)m", "(CH3CN)n(CH3OH)m", "[C3H6O]nH2O", "C5H10NO", "[C2H4O]nH2O",
      "C2H6OS", "[MeOH]n[H2O]m", "(CH3CN)n(CH3COOH)m", "C6H15N", "C2H3N", "C2H3O2Na", "C2H3N",
      "C2H3N", "[C2H4O]nH2O", "[C3H6O]nH2O", "(CH3CN)n(C3H7NO)m", "C2H6OS", "C4H11NO3",
      "[MeOH]n[H2O]m", "C7H10N2", "(CH3CN)n(CH3COOH)m", "[C2H4O]nH2O", "C8H19N", "Cs",
      "[MeOH]n[H2O]m", "[C3H6O]nH2O", "C2H6OS", "C2H6OS", "C9H21N", "CH3CN", "[C2H4O]nH2O", "CH3CN",
      "CH3CN", "(CH3CN)n(CH3OH)m", "C8H4O3", "C10H15N", "[C2H4O]nH2O", "C9H16N2", "[MeOH]n[H2O]m",
      "C2H6OS", "[C3H6O]nH2O", "C2F3O2Na", "C10H10O4", "C8H18O3", "(CH3CN)n(CH3OH)m", "C2D6OS",
      "C8H4O3", "C10H7NO3", "[C3H6O]nH2O", "[C2H4O]nH2O", "C2H6OS", "C11H16O2", "C13H10O",
      "[MeOH]n[H2O]m", "C8H18O3", "C12H27N", "[C2H4O]nH2O", "C10H7NO3", "[C3H6O]nH2O", "C10H10O4",
      "[C2H4O]nH2O", "C11H16O2", "[MeOH]n[H2O]m", "C10H7NO3", "C10H15NO2S", "[C3H6O]nH2O",
      "[C2H4O]nH2O", "C15H24O", "C13H24N2O", "C10H7NO3", "[C3H6O]nH2O", "C10H15NO2S", "[C2H4O]nH2O",
      "C10H15NO2S", "[C2H4O]nH2O", "C6H15N", "C6H15N", "C16H36N", "C19H15", "C15H24O",
      "[C3H6O]nH2O", "[C14H22O][C2H4O]n", "C2H6OS", "[C2H4O]nH2O", "[C15H24O][C2H4O]n", "C12H27O4P",
      "C20H17O", "[C3H6O]nH2O", "[C14H22O][C2H4O]n", "[C2H4O]nH2O", "C18H15OP", "C16H22O4",
      "[C14H28O][C2H4O]n", "C18H35NO", "[C2H4O]nH2O", "C18H37NO", "[C15H24O][C2H4O]n", "C16H33NO3",
      "[C3H6O]nH2O", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "C16H22O4", "C18H35NO",
      "[C2H4O]nH2O", "C18H37NO", "[C3H6O]nH2O", "[C15H24O][C2H4O]n", "C18H34O4", "C16H22O4",
      "[C14H22O][C2H4O]n", "[C2H4O]nH2O", "[C14H28O][C2H4O]n", "C8H18O3", "C18H15O4P",
      "[C2H4O]nH2O", "[C3H6O]nH2O", "[C15H24O][C2H4O]n", "C13H28O2Sn", "[C15H30O][C2H4O]n",
      "C22H43NO", "[C14H22O][C2H4O]n", "[C3H6O]nH2O", "[C2H4O]nH2O", "[C15H24O][C2H4O]n",
      "[C2H6SiO]5", "C22H47N2OCl", "C22H43NO", "[C14H22O][C2H4O]n", "[C2H4O]nH2O", "[C3H6O]nH2O",
      "[C14H28O][C2H4O]n", "C25H54NCl", "[C2H6SiO]5", "[C2H4O]nH2O", "C22H42O4", "C22H42O4",
      "[C15H24O][C2H4O]n", "C10H7NO3", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "[C2H6SiO]5",
      "[C3H6O]nH2O", "C24H38O4", "[C2H4O]nH2O", "[C15H24O][C2H4O]n", "[C3H6O]nH2O",
      "[C14H22O][C2H4O]n", "[C2H4O]nH2O", "[C14H28O][C2H4O]n", "C24H38O4", "[C2H4O]nH2O",
      "[C15H24O][C2H4O]n", "[C3H6O]nH2O", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "[C2H6SiO]6",
      "C24H38O4", "[C2H4O]nH2O", "C10H7NO3", "[C15H24O][C2H4O]n", "C10H7NO3", "[C2H6SiO]6",
      "[C3H6O]nH2O", "[C14H22O][C2H4O]n", "C13H24N2O", "[C2H4O]nH2O", "C24H44N4O4", "C24H38O4",
      "[C14H28O][C2H4O]n", "[C2H4O]nH2O", "[C2H6SiO]6", "[C3H6O]nH2O", "[C15H24O][C2H4O]n",
      "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "SLPR", "[C2H4O]nH2O", "[C3H6O]nH2O",
      "[C15H24O][C2H4O]n", "[C14H22O][C2H4O]n", "C34H72NCl", "[C2H4O]nH2O", "[C14H28O][C2H4O]n",
      "[C2H6SiO]7", "[C2H4O]nH2O", "[C3H6O]nH2O", "[C15H24O][C2H4O]n", "[C15H30O][C2H4O]n", "IQVR",
      "[C14H22O][C2H4O]n", "C30H58O4S", "[C2H6SiO]7", "[C3H6O]nH2O", "C36H76NCl", "[C2H4O]nH2O",
      "[C15H24O][C2H4O]n", "C30H58O5S", "C35H62O3", "[C2H6SiO]7", "[C14H22O][C2H4O]n", "C2H4O2",
      "[C2H4O]nH2O", "[C3H6O]nH2O", "[C14H28O][C2H4O]n", "[C2H4O]nH2O", "C30H58O6S", "C38H80NCl",
      "[C15H24O][C2H4O]n", "C30H58O5S", "C35H62O3", "C2H4O2", "[C15H30O][C2H4O]n",
      "[C14H22O][C2H4O]n", "[C3H6O]nH2O", "C10H7NO3", "[C2H4O]nH2O", "VSLPR", "[C15H24O][C2H4O]n",
      "[C2H6SiO]8", "[C3H6O]nH2O", "[C14H22O][C2H4O]n", "[C2H4O]nH2O", "[C14H28O][C2H4O]n",
      "[C2H4O]nH2O", "[C2H6SiO]8", "[C15H24O][C2H4O]n", "C2H4O2", "[C3H6O]nH2O",
      "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "C10H7NO3", "[C2H6SiO]8", "[C2H4O]nH2O",
      "C32H58N2O7S", "[C15H24O][C2H4O]n", "[C3H6O]nH2O", "C3H6O2", "[C14H22O][C2H4O]n",
      "[C2H4O]nH2O", "[C14H28O][C2H4O]n", "QTIASN", "[C2H4O]nH2O", "[C3H6O]nH2O",
      "[C15H24O][C2H4O]n", "[C15H30O][C2H4O]n", "GLTAER (from YLDGLTAER)", "[C14H22O][C2H4O]n",
      "[C2H6SiO]9", "[C2H4O]nH2O", "[C3H6O]nH2O", "SGIQVR", "[C15H24O][C2H4O]n", "[C2H6SiO]9",
      "[C14H22O][C2H4O]n", "TVSLPR", "TVSLPR", "[C2H4O]nH2O", "[C14H28O][C2H4O]n", "[C2H4O]nH2O",
      "[C3H6O]nH2O", "C36H66N6O6", "[C15H24O][C2H4O]n", "[C2H6SiO]9", "[C15H30O][C2H4O]n",
      "[C14H22O][C2H4O]n", "[C3H6O]nH2O", "[C2H4O]nH2O", "LDSELK", "[C15H24O][C2H4O]n",
      "[C14H22O][C2H4O]n", "[C3H6O]nH2O", "[C2H4O]nH2O", "[C14H28O][C2H4O]n", "[C2H4O]nH2O",
      "[C2H6SiO]10", "[C15H24O][C2H4O]n", "GLVLIAF", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n",
      "[C3H6O]nH2O", "[C2H6SiO]10", "GPFPILV", "ATVSLPR", "[C2H4O]nH2O", "[C15H24O][C2H4O]n",
      "[C3H6O]nH2O", "[C14H22O][C2H4O]n", "[C2H6SiO]10", "PATLNSR", "[C2H4O]nH2O",
      "[C14H28O][C2H4O]n", "[C2H4O]nH2O", "[C15H24O][C2H4O]n", "[C3H6O]nH2O", "[C15H30O][C2H4O]n",
      "[C14H22O][C2H4O]n", "[C2H4O]nH2O", "[C15H24O][C2H4O]n", "[C3H6O]nH2O", "C24H38O4",
      "[C14H22O][C2H4O]n", "LSSPATLN", "C24H38O4", "SEIDNVK", "SAASLNSR", "[C2H4O]nH2O", "LAADDFR",
      "[C14H28O][C2H4O]n", "LASYLDK", "[C18H34O6][C2H4O]n", "[C2H4O]nH2O", "[C3H6O]nH2O",
      "[C15H24O][C2H4O]n", "C24H38O4", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "PGVVSLPR",
      "FASFIDK", "PEIQNVK", "[C3H6O]nH2O", "SISISVAR", "[C2H4O]nH2O", "[C15H24O][C2H4O]n",
      "C10H7NO3", "VATVSLPR", "C10H7NO3", "[C14H22O][C2H4O]n", "AFIDKVR", "[C2H4O]nH2O",
      "[C14H28O][C2H4O]n", "[C18H34O6][C2H4O]n", "[C3H6O]nH2O", "C10H7NO3", "[C2H4O]nH2O",
      "[C15H24O][C2H4O]n", "C10H7NO3", "SFLINNR (from THNLEPYFESFINNLR)",
      "IEIATYR (from LALDIEIATYR)", "[C22H42O6][C2H4O]n", "[C15H30O][C2H4O]n", "C10H7NO3",
      "[C14H22O][C2H4O]n", "[C3H6O]nH2O", "VATVSLPR  N-term. methylated ", "C10H7NO3", "QATVSLPR",
      "SLVNLGGSK", "[C2H4O]nH2O", "SLYGLGGSK", "[C15H24O][C2H4O]n", "RVYVHPI", "[C14H22O][C2H4O]n",
      "[C3H6O]nH2O", "[C24H44O6][C2H4O]n", "[C2H4O]nH2O", "[C24H46O6][C2H4O]n", "[C14H28O][C2H4O]n",
      "[C18H34O6][C2H4O]n", "VQTVSLPR", "[C2H4O]nH2O", "[C15H24O][C2H4O]n", "C48H88N8O8",
      "NKPGVYTK", "NKPGVYTK", "[C22H42O6][C2H4O]n", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n",
      "[C3H6O]nH2O", "RVYVHPF", "[C2H4O]nH2O", "[C15H24O][C2H4O]n", "[C3H6O]nH2O", "RVYIHPF",
      "[C14H22O][C2H4O]n", "[C24H44O6][C2H4O]n", "[C2H4O]nH2O", "[C24H46O6][C2H4O]n",
      "[C14H28O][C2H4O]n", "[C18H34O6][C2H4O]n", "[C15H24O][C2H4O]n", "[C3H6O]nH2O", "YVNWIQQ",
      "[C22H42O6][C2H4O]n", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "[C15H24O][C2H4O]n",
      "[C3H6O]nH2O", "IEISELNR", "[C14H22O][C2H4O]n", "GTSYPDVLK", "[C24H44O6][C2H4O]n",
      "[C24H46O6][C2H4O]n", "[C14H28O][C2H4O]n", "[C18H34O6][C2H4O]n", "[C3H6O]nH2O",
      "[C15H24O][C2H4O]n", "C10H7NO3", "IKEWYEK", "[C22H42O6][C2H4O]n", "[C15H30O][C2H4O]n",
      "[C14H22O][C2H4O]n", "LQAEIEGLK", "LVVSTQTALA, C-terminus", "SEITELRR", "[C3H6O]nH2O",
      "[C15H24O][C2H4O]n", "APILSDSSCK", "SIPYQVSLN", "[C14H22O][C2H4O]n", "SSYPGQITGN",
      "NEQFISASK", "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "[C14H28O][C2H4O]n", "[C3H6O]nH2O",
      "[C18H34O6][C2H4O]n", "VLDELTLTK", "TLLEGEESR", "[C15H24O][C2H4O]n", "C10H7NO3", "IRDWYQR",
      "YLDGLTAER", "[C22H42O6][C2H4O]n", "[C15H30O][C2H4O]n", "[C14H22O][C2H4O]n", "[C3H6O]nH2O",
      "C10H7NO3", "LSSPATLNSR", "LKSAASLNSR", "C10H7NO3", "[C15H24O][C2H4O]n", "C10H7NO3",
      "TLLDIDNTR", "[C3H6O]nH2O", "DIIRAVGAYS", "LASYLDKVR", "STMQELNSR", "[C14H22O][C2H4O]n",
      "C10H7NO3", "FAAFIDKVR", "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "LAGLEEALQK",
      "[C14H28O][C2H4O]n", "[C18H34O6][C2H4O]n", "[C15H24O][C2H4O]n",
      "GNEQFISASK (from LGEDNINVVEGNEQFISASK)", "FASFIDKVR", "[C22H42O6][C2H4O]n",
      "[C15H30O][C2H4O]n", "[C3H6O]nH2O", "[C14H22O][C2H4O]n", "VTMQNLNDR", "GNEQFINAAK",
      "[C15H24O][C2H4O]n", "[C3H6O]nH2O", "DAEAWFNEK", "[C14H22O][C2H4O]n", "VCNYVSWIK",
      "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "[C14H28O][C2H4O]n", "[C18H34O6][C2H4O]n",
      "HGNSHQGEPR", "QEYEQLIAK", "[C3H6O]nH2O", "[C15H24O][C2H4O]n", "IITHPNFNGN", "LSELEAALQR",
      "[C22H42O6][C2H4O]n", "[C15H30O][C2H4O]n", "DYQELMNTK", "VCNYVSWIK (W - oxid.)",
      "[C3H6O]nH2O", "SSGTSYPDVLK", "[C24H44O6][C2H4O]n", "QGVDADINGLR", "[C24H46O6][C2H4O]n",
      "[C3H6O]nH2O", "[C18H34O6][C2H4O]n", "LENEIQTYR", "VCcamNYVSWIK", "[C22H42O6][C2H4O]n",
      "MFCAGYLEGGK", "TLDNDIMLIK", "YEELQITAGR", "[C3H6O]nH2O", "NVEIDPEIQK", "QVLDNLTMEK",
      "MoxFCAGYLEGGK", "TLDNDIMoxLIK", "YEELQVTVGR", "[C24H44O6][C2H4O]n",
      "VCcamNYVSWIK (W - oxid.)", "[C24H46O6][C2H4O]n", "[C3H6O]nH2O", "TLDNDIMLIR",
      "[C18H34O6][C2H4O]n", "TAAENDFVTLK", "SSGGTSYPDVLK", "SSGGTSYPDVLK", "[C22H42O6][C2H4O]n",
      "[C3H6O]nH2O", "TLDNDIMoxLIR", "EGNEQFINAAK", "C10H7NO3", "C32H58N2O7S", "MFCcamAGYLEGGK",
      "SGGGGGGGLGSGGSIR", "FSSSSGYGGGSSR", "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n",
      "MoxFCcamAGYLEGGK", "[C18H34O6][C2H4O]n", "YIPIQYVLSR", "GFSSGSAVVSGGSR",
      "[C22H42O6][C2H4O]n", "SLLEGEGSSGGGGR", "LLHGVATVSLPR", "TNAENEFVTIK", "YLGYLEQLLR",
      "SDQSRLDSELK", "LALDIEIATYR", "GSCcamGIGGGIGGGSSR", "YEQLAEQNRK", "DRVYVHPFHL",
      "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "[C18H34O6][C2H4O]n", "DRVYIHPFHL",
      "ALEEANADLEVK", "NSKIEISELNR", "SLDLDSIIAEVK", "HLVDEPQNLIK", "[C22H42O6][C2H4O]n",
      "IKFEMEQNLR ", "VEGNEQFISASK", "NKYEDEINKR", "DQIVDLTVGNNK", "TLDNDIMLIRL",
      "HGGGGGGFGGGGFGSR", "IKFEMoxEQNLR ", "NVQDAIADAEQR", "[C24H44O6][C2H4O]n", "TLDNDIMoxLIRL",
      "[C24H46O6][C2H4O]n", "TAAENDFVTLKK", "[C18H34O6][C2H4O]n", "SKAEAESLYQSK", "ASLEAAIADAEQR",
      "[C22H42O6][C2H4O]n", "IGLGGRGGSGGSYGR", "LNDLEDALQQAK", "QSVEADINGLRR", "SQYEQLAEQNR",
      "RISNSTSPTSFVA", "VFTSWTGGGAAASR", "LNDLEEALQQAK", "DLNMDNIVAEIK", "[C24H44O6][C2H4O]n",
      "[C24H46O6][C2H4O]n", "ALEESNYELEGK", "[C18H34O6][C2H4O]n", "SLNNQFASFIDK", "FFVAPFPEVFGK",
      "QSLEASLAETEGR", "TNAENEFVTIKK", "[C22H42O6][C2H4O]n", "TVMENFVAFVDK",
      "PYFESFINNLR (from THNLEPYFESFINNLR)", "VDALNDEINFLR", "LEGLTDEINFLR", "LGLDIEIATYRR",
      "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "AAKIITHPNFNGN", "[C18H34O6][C2H4O]n",
      "LQGIVSWGSGCAQK", "IRLENEIQTYR", "[C22H42O6][C2H4O]n", "NNQFASFIDKVR", "RHPEYAVSVLLR",
      "LPDIFEAQIAGLR", "YICcamDNQDTISSK", "AIGGGLSSVGGGSSTIK", "LQGIVSWGSGCAQK (W - oxid. I)",
      "EVTINQSLLAPLR", "[C24H44O6][C2H4O]n", "LQGIVSWGSGCAQK (W - oxid. II)", "[C24H46O6][C2H4O]n",
      "[C18H34O6][C2H4O]n", "WELLQQVDTSTR", "FLEQQNQVLQTK", "FLEQQNKVLETK", "LGEYGFQNALIVR",
      "[C22H42O6][C2H4O]n", "LQGIVSWGSGCcamAQK", "WELLQQVDTSTR (W - oxid. I)", "FYAPELLYYANK",
      "DSCQGDSGGPVVCSGK ox. S-S bond", "SQYEQLAEQNRK", "DSCQGDSGGPVVCSGK",
      "WELLQQVDTSTR (W - oxid. II)", "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n",
      "[C18H34O6][C2H4O]n", "NVVEGNEQFISASK", "LQGIVSWoxGSGCcamAQK", "[C22H42O6][C2H4O]n",
      "SGGGGGGGGCcamGGGGGVSSLR", "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "[C18H34O6][C2H4O]n",
      "LGEHNIDVLEGNEQ", "DAFLGSFLYEYSR", "[C22H42O6][C2H4O]n", "VQALEEANNDLENK",
      "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "NKLNDLEDALQQAK", "[C18H34O6][C2H4O]n",
      "[C22H42O6][C2H4O]n", "EQFINAAKIITHPN", "SLNNQFASFIDKVR", "[C24H44O6][C2H4O]n",
      "KVPQVSTPTLVEVSR", "[C24H46O6][C2H4O]n", "[C18H34O6][C2H4O]n", "SGGGFSSGSAGIINYQR",
      "[C22H42O6][C2H4O]n", "TKPSQARGFHPRAGR", "LGEDNINVVEGNEQF", "[C24H44O6][C2H4O]n",
      "[C24H46O6][C2H4O]n", "SLVNLGGSKSISISVAR", "[C18H34O6][C2H4O]n", "[C22H42O6][C2H4O]n",
      "GSLGGGFSSGGFSGGSFSR", "FSSCGGGGGSFGAGGGFGSR", "LGEHNIDVLEGNEQF", "QISNLQQSISDAEQR",
      "VCNYVSWIKQTIASN", "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "[C18H34O6][C2H4O]n",
      "NNLEPILEGYISNLR", "[C22H42O6][C2H4O]n", "VCNYVSWIKQTIASN (W - oxid.)",
      "FSSCcamGGGGGSFGAGGGFGSR", "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n",
      "IVGGYTCGANTVPY & CLK via S-S bond", "NIDVLEGNEQFINAAK", "[C18H34O6][C2H4O]n",
      "[C22H42O6][C2H4O]n", "GGSGGSYGGGGSGGGYGGGSGSR", "LEAELGNMQGLVEDFK", "NVQALEIELQSQLALK",
      "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "SDQYGRVFTSWTGGGAAA", "[C22H42O6][C2H4O]n",
      "HGVQELEIELQSQLSK", "SISISVAGGGGGFGAAGGFGGR", "SNMDNMFESYINNLR", "TLNDMRQEYEQLIAK",
      "[C24H44O6][C2H4O]n", "[C24H46O6][C2H4O]n", "LGEHNIDVLEGNEQFIN",
      "TLDNDIMLIKLSSPATLN, K methylated", "THNLEPYFESFINNLR", "SSGGSSSVKFVSTTYSGVTR",
      "TLDNDIMoxLIKLSSPATLN, K methylated", "SSGGSSSVRFVSTTYSGVTR ", "RVLGQLHGGPSSCcamSATGTNR",
      "AETECcamQNTEYQQLLDIK", "LGEHNIDVLEGNEQFINAA", "ELQSQISDTSVVLSMDNSR", "THNLEPYFESFINNLRR",
      "IITHPNFNGNTLDNDIMLI", "LGEDNINVVEGNEQFISASK", "SDLEMQYETLQEELMALK", "NKLNDLEDALQQAKEDLAR",
      "LGEDNINVVEGNEQFISASK sodiated", "MFCAGYLEGGK & APILSDSSCK via S-S bond",
      "SAYPGQITSNMFCAGYLEGGK", "LGEDNINVVEGNEQFISASK potassiated",
      "MoxFCAGYLEGGK & APILSDSSCK via S-S bond", "SAYPGQITSNMoxFCAGYLEGGK ", "LGEHNIDVLEGNEQFINAAK",
      "LGEHNIDVLEGNEQFINAAK, K part. methylated", "SIVHPSYNSNTLNNDIMoxLIK -[CH3SOH]",
      "LGEHNIDVLEGNEQFINAAK, K methylated", "ADLEFQIESLTEELAYLKK", "SAYPGQITSNMFCcamAGYLEGGK",
      "SAYPGQITSNMoxFCcamAGYLEGGK", "SIVHPSYNSNTLNNDIMLIK", "IITHPNFNGNTLDNDIMLIK",
      "IITHPNFNGNTLDNDIMLIK", "AEAESLYQSKYEELQITAGR", "SIVHPSYNSNTLNNDIMoxLIK (M - oxid.)",
      "SIVHPSYNSNTLNNDIMLIK sodiated", "IITHPNFNGNTLDNDIMLIK, K part. methylated",
      "SIVHPSYNSNTLNNDIMoxoxLIK (M - 2 oxid.)", "SIVHPSYNSNTLNNDIMoxLIK (M - oxid.) sodiated",
      "LQGIVSWGSGCAQKNKPGVYTK", "QISNLQQSISDAEQRGENALK", "LQGIVSWGSGCAQKNKPGVYTK sodiated",
      "LQGIVSWGSGCAQKNKPGVYTK (W - oxid.)", "NQILNLTTDNANILLQIDNAR",
      "LQGIVSWGSGCAQKNKPGVYTK (W - oxid.) sodiated", "GGGGGGYGSGGSSYGSGGGSYGSGGGGGGGR",
      "IIKHPNYSSWTLNNDIMLIK", "AAFGGSGGRGSSSGGGYSSGSSSYGSGGR", "SKAEAESLYQSKYEELQITAGR",
      "EIETYHNLLEGGQEDFESSGAGK", "SIVHPSYNSNTLNNDIMLIKLK", "SIVHPSYNSNTLNNDIMoxLIKLK",
      "VASISLPTSCASAGTQCLISGWGNTK, -S-S-bond", "VASISLPTSCASAGTQCLISGWGNTK ",
      "SGSHFCGGSLINSQWVVSAAHCYK, -S-S- bond", "EVTQLRHGVQELEIELQSQLSK", "SKEEAEALYHSKYEELQVTVGR",
      "FSGECcamAPNVSVSVSTSHTTISGGGSR", "SGYHFCGGSLINSQWVVSAAHCYK, -S-S- bond",
      "NKPGVYTKVCNYVSWIKQTIASN", "SGYHFCGGSLINSQWVVSAAHCYK", "VASISLPTSCcamASAGTQCcamLISGWGNTK ",
      "NKPGVYTKVCcamNYVSWIKQTIASN", "GGGGSFGYSYGGGSGGGFSASSLGGGFGGGSR", "IQVRLGEHNIDVLEGNEQFINAAK",
      "IVGGYTCAAN & TKSSGSSYPSLLQCLK, via S-S bond", "SGYHFCcamGGSLINSQWVVSAAHCcamYK",
      "YCcamVQLSQIQAQISALEEQLQQIR", "IVGGYTCAAN & TKSSGSSYPSLLQCLK, via S-S bond, N-term. Methyl.",
      "SGSHFCGGSLINSQWVVSAAHCYKSR, -S-S- bond", "NVSTGDVNVEMNAAPGVDLTQLLNNMR",
      "NYSPYYNTIDDLKDQIVDLTVGNNK", "NVSTGDVNVEMoxNAAPGVDLTQLLNNMoxR",
      "LGEHNIDVLEGNEQFINAAKIITHPN, K methylated", "FLEQQNQVLQTKWELLQQVDTSTR",
      "SAYPGQITSNMFCAGYLEGGK & APILSDSSCK via S-S bond", "GGSGGSHGGGSGFGGESGGSYGGGEEASGSGGGYGGGSGK",
      "SAYPGQITSNMoxFCAGYLEGGK & APILSDSSCK via S-S bond",
      "GSYGSGGSSYGSGGGSYGSGGGGGGHGSYGSGSSSGGYR", "IITHPNFNGNTLDNDIMLIRLSSPATLNSR",
      "LGEHNIDVLEGNEQFINAAKIITHPNFNGN, K methylated", "IITHPNFNGNTLDNDIMoxLIRLSSPATLNSR"};

  private String[] formulaNeg = new String[] {"CN", "HCOOH", "CH3COOH", "H3PO4", "H2SO4", "CH3SO3H",
      "H2SO4", "H3PO4", "CF3COOH", "HCOOH", "I", "CF3CF2COOH", "C10H15NO2S", "CF3COOH", "CF3COOH",
      "C16H32O2", "C18H34O2", "C18H36O2", "CuI", "CuI"};

  private String[] compoundIDPos = new String[] {"Methanol", "ACN", "ACN", "PEG", "ACN", "Methanol",
      "Dimethyl formamide", "Acetonitrile/Methanol", "PPG", "DMSO", "Acetonitrile", "PEG",
      "d6-DMSO", "Acetonitrile/Formic Acid", "Acetonitrile/Methanol", "PPG", "NMP", "PEG", "DMSO",
      "Methanol/Water", "Acetonitrile/Acetic Acid", "TEA", "ACN", "Sodium acetate", "ACN", "ACN",
      "PEG", "PPG", "Acetonitrile/Dimethylformamide", "DMSO", "TRIS", "Methanol/Water", "DMAP",
      "Acetonitrile/Acetic Acid", "PEG", "DIPEA", "Cs-133", "Methanol/Water", "PPG", "DMSO", "DMSO",
      "TPA", "ACN", "PEG", "ACN", "ACN", "Acetonitrile/Methanol", "Pthalic Anhydride",
      "Phenyldiethylamine", "PEG", "DBU", "Methanol/Water", "DMSO", "PPG", "NaTFA",
      "Dimethyl phthalate", "DGBE", "Acetonitrile/Methanol", "d6-DMSO", "Phthalic anhydride",
      "4-HCCA", "PPG", "PEG", "DMSO", "BHA", "DPK", "Methanol/Water", "GE", "TBA", "PEG", "4-HCCA",
      "PPG", "Dimethyl phthalate", "PEG", "BHA", "Methanol/Water", "4-HCCA", "n-BBS", "PPG", "PEG",
      "BTH", "DCU", "4-HCCA", "PPG", "n-BBS", "PEG", "n-BBS", "PEG", "TEA.HCl", "TEA.HCl", "TBA",
      "Trityl cation", "BTH", "PPG", "Triton", "DMSO", "PEG", "Triton", "TBP", "MMT", "PPG",
      "Triton", "PEG", "TPO", "Dibutylphthalate", "Triton, reduced", "Oleamide", "PEG",
      "Stearamide", "Triton", "n,n-DDA", "PPG", "Triton, reduced", "Triton", "Dibutylphthalate",
      "Oleamide", "PEG", "Stearamide", "PPG", "Triton", "DBS", "Dibutylphthalate", "Triton", "PEG",
      "Triton, reduced", "DGBE", "TPP", "PEG", "PPG", "Triton", "Tributyl tin formate",
      "Triton, reduced", "Erucamide", "Triton", "PPG", "PEG", "Triton", "Polysiloxane", "",
      "Erucamide", "Triton", "PEG", "PPG", "Triton, reduced", "BTAC-228", "Polysiloxane", "PEG",
      "DEHA", "DOA", "Triton", "4-HCCA", "Triton, reduced", "Triton", "Polysiloxane", "PPG",
      "Diisooctyl phthalate", "PEG", "Triton", "PPG", "Triton", "PEG", "Triton, reduced",
      "Diisooctyl phthalate", "PEG", "Triton", "PPG", "Triton, reduced", "Triton", "Polysiloxane",
      "Diisooctyl phthalate", "PEG", "4-HCCA", "Triton", "4-HCCA", "Polysiloxane", "PPG", "Triton",
      "DCU", "PEG", "nylon ", "Diisooctyl phthalate", "Triton, reduced", "PEG", "Polysiloxane",
      "PPG", "Triton", "Triton, reduced", "Triton", "Peptide", "PEG", "PPG", "Triton", "Triton",
      "DPDMA", "PEG", "Triton, reduced", "Polysiloxane", "PEG", "PPG", "Triton", "Triton, reduced",
      "Peptide", "Triton", "DDTDP", "Polysiloxane", "PPG", "SPDMA", "PEG", "Triton", "DDTDP",
      "Irganox", "Polysiloxane", "Triton", "Acetic acid-Fe-O- complex", "PEG", "PPG",
      "Triton, reduced", "PEG", "DDTDP", "DSDMA", "Triton", "DDTDP", "Irganox",
      "Acetic acid-Fe-O- complex", "Triton, reduced", "Triton", "PPG", "4-HCCA", "PEG", "Peptide",
      "Triton", "Polysiloxane", "PPG", "Triton", "PEG", "Triton, reduced", "PEG", "Polysiloxane",
      "Triton", "Acetic acid-Fe-O- complex", "PPG", "Triton, reduced", "Triton", "4-HCCA",
      "Polysiloxane", "PEG", "CHAPS", "Triton", "PPG", "Propionic acid Fe-O complex", "Triton",
      "PEG", "Triton, reduced", "Peptide", "PEG", "PPG", "Triton", "Triton, reduced", "Peptide",
      "Triton", "Polysiloxane", "PEG", "PPG", "Peptide", "Triton", "Polysiloxane", "Triton",
      "Peptide", "Peptide", "PEG", "Triton, reduced", "PEG", "PPG", "nylon ", "Triton",
      "Polysiloxane", "Triton, reduced", "Triton", "PPG", "PEG", "Peptide", "Triton", "Triton",
      "PPG", "PEG", "Triton, reduced", "PEG", "Polysiloxane", "Triton", "Peptide",
      "Triton, reduced", "Triton", "PPG", "Polysiloxane", "Peptide", "Peptide", "PEG", "Triton",
      "PPG", "Triton", "Polysiloxane", "Peptide", "PEG", "Triton, reduced", "PEG", "Triton", "PPG",
      "Triton, reduced", "Triton", "PEG", "Triton", "PPG", "Diisooctyl phthalate", "Triton",
      "Peptide", "Diisooctyl phthalate", "Peptide", "Peptide", "PEG", "Peptide", "Triton, reduced",
      "Peptide", "Tween", "PEG", "PPG", "Triton", "Diisooctyl phthalate", "Triton, reduced",
      "Triton", "Peptide", "Peptide", "Peptide", "PPG", "Peptide", "PEG", "Triton", "4-HCCA",
      "Peptide", "4-HCCA", "Triton", "Peptide", "PEG", "Triton, reduced", "Tween", "PPG", "4-HCCA",
      "PEG", "Triton", "4-HCCA", "Peptide", "Peptide", "Tween", "Triton, reduced", "4-HCCA",
      "Triton", "PPG", "Peptide", "4-HCCA", "Peptide", "Peptide", "PEG", "Peptide", "Triton",
      "Peptide", "Triton", "PPG", "Tween", "PEG", "Tween", "Triton, reduced", "Tween", "Peptide",
      "PEG", "Triton", "nylon ", "Peptide", "Peptide", "Tween", "Triton, reduced", "Triton", "PPG",
      "Peptide", "PEG", "Triton", "PPG", "Peptide", "Triton", "Tween", "PEG", "Tween",
      "Triton, reduced", "Tween", "Triton", "PPG", "Peptide", "Tween", "Triton, reduced", "Triton",
      "Triton", "PPG", "Peptide", "Triton", "Peptide", "Tween", "Tween", "Triton, reduced", "Tween",
      "PPG", "Triton", "4-HCCA", "Peptide", "Tween", "Triton, reduced", "Triton", "Peptide",
      "Peptide", "Peptide", "PPG", "Triton", "Peptide", "Peptide", "Triton", "Peptide", "Peptide",
      "Tween", "Tween", "Triton, reduced", "PPG", "Tween", "Peptide", "Peptide", "Triton", "4-HCCA",
      "Peptide", "Peptide", "Tween", "Triton, reduced", "Triton", "PPG", "4-HCCA", "Peptide",
      "Peptide", "4-HCCA", "Triton", "4-HCCA", "Peptide", "PPG", "Peptide", "Peptide", "Peptide",
      "Triton", "4-HCCA", "Peptide", "Tween", "Tween", "Peptide", "Triton, reduced", "Tween",
      "Triton", "Peptide", "Peptide", "Tween", "Triton, reduced", "PPG", "Triton", "Peptide",
      "Peptide", "Triton", "PPG", "Peptide", "Triton", "Peptide", "Tween", "Tween",
      "Triton, reduced", "Tween", "Peptide", "Peptide", "PPG", "Triton", "Peptide", "Peptide",
      "Tween", "Triton, reduced", "Peptide", "Peptide", "PPG", "Peptide", "Tween", "Peptide",
      "Tween", "PPG", "Tween", "Peptide", "Peptide", "Tween", "Peptide", "Peptide", "Peptide",
      "PPG", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Tween", "Peptide", "Tween",
      "PPG", "Peptide", "Tween", "Peptide", "Peptide", "Peptide", "Tween", "PPG", "Peptide",
      "Peptide", "4-HCCA", "CHAPS", "Peptide", "Peptide", "Peptide", "Tween", "Tween", "Peptide",
      "Tween", "Peptide", "Peptide", "Tween", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Tween", "Tween", "Tween", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Tween", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Tween", "Peptide", "Tween", "Peptide", "Tween",
      "Peptide", "Peptide", "Tween", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Tween", "Tween", "Peptide", "Tween", "Peptide", "Peptide",
      "Peptide", "Peptide", "Tween", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Tween",
      "Tween", "Peptide", "Tween", "Peptide", "Peptide", "Tween", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Tween", "Peptide", "Tween", "Tween", "Peptide",
      "Peptide", "Peptide", "Peptide", "Tween", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Tween", "Tween", "Tween", "Peptide", "Peptide", "Tween",
      "Peptide", "Tween", "Tween", "Tween", "Peptide", "Peptide", "Tween", "Peptide", "Tween",
      "Tween", "Peptide", "Tween", "Tween", "Peptide", "Peptide", "Tween", "Peptide", "Tween",
      "Tween", "Peptide", "Tween", "Peptide", "Peptide", "Tween", "Tween", "Peptide", "Tween",
      "Tween", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Tween", "Tween", "Tween",
      "Peptide", "Tween", "Peptide", "Peptide", "Tween", "Tween", "Peptide", "Peptide", "Tween",
      "Tween", "Peptide", "Peptide", "Peptide", "Tween", "Tween", "Peptide", "Tween", "Peptide",
      "Peptide", "Peptide", "Peptide", "Tween", "Tween", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide", "Peptide",
      "Peptide", "Peptide", "Peptide", "Peptide"};

  private String[] compoundIDNeg = null;

  private String[] possibleOriginPos = new String[] {"Acetonitrile, solvent",
      "Acetonitrile, solvent", "Acetonitrile, solvent", "Polyethylene glycol, ubiquitous polyether",
      "Acetonitrile, solvent", "Methanol, solvent", "solvent", "ESI solvents",
      "Polypropylene glycol, ubiquitous polyether", "Dimethylsulfoxide, solvent", "ESI solvents",
      "Polyethylene glycol, ubiquitous polyether", "d6-Dimethylsulfoxide, solvent", "ESI solvents",
      "ESI solvents", "Polypropylene glycol, ubiquitous polyether",
      "N-methyl 2-pyrrolidone; solvent, floor stripper",
      "Polyethylene glycol, ubiquitous polyether", "Dimethylsulfoxide, solvent", "ESI solvents",
      "ESI solvents", "Triethylamine, buffer", "Acetonitrile, solvent", "ESI solvents",
      "Acetonitrile, solvent", "Acetonitrile, solvent", "Polyethylene glycol, ubiquitous polyether",
      "Polypropylene glycol, ubiquitous polyether", "solvent", "Dimethylsulfoxide, solvent",
      "TRIS, buffer", "ESI solvents", "Dimethylaminopyridine, solvent", "ESI solvents",
      "Polyethylene glycol, ubiquitous polyether", "Diisopropylethylamine, solvent",
      "Cesium, from Cesium Iodide used as calibrant", "ESI solvents",
      "Polypropylene glycol, ubiquitous polyether", "Dimethylsulfoxide, solvent",
      "Dimethylsulfoxide, solvent", "Tripropylamine, solvent",
      "Acetonitrile, solvent, together with m/z 147", "Polyethylene glycol, ubiquitous polyether",
      "Acetonitrile, solvent", "Acetonitrile, solvent, together with m/z 145", "ESI solvents",
      "fragment ion originating from phthalate esters", "solvent",
      "Polyethylene glycol, ubiquitous polyether", "1,8-Diazabicyclo[5.4.0]undec-7-ene",
      "ESI solvents", "Dimethylsulfoxide, solvent", "Polypropylene glycol, ubiquitous polyether",
      "Sodium trifluoroacetate, salt", "Phthalate esters, plasticizer",
      "Diethylene glycol monobutyl ether, cpd. In scintillation cocktail", "ESI solvents",
      "d6-Dimethylsulfoxide, solvent", "from phthalate esters, plasticizer", "matrix compound",
      "Polypropylene glycol, ubiquitous polyether", "Polyethylene glycol, ubiquitous polyether",
      "Dimethylsulfoxide, solvent", "Butylated hydroxyanisole, antioxidant additives",
      "Diphenyl ketone", "ESI solvents", "glycol ether", "Tributylamine, solvent",
      "Polyethylene glycol, ubiquitous polyether", "matrix compound",
      "Polypropylene glycol, ubiquitous polyether", "Phthalate esters, plasticizer",
      "Polyethylene glycol, ubiquitous polyether",
      "Butylated hydroxyanisole, antioxidant additives", "ESI solvents",
      "matrix compound, sodiated", "n-butyl benzenesulfonamide, plasticizer",
      "Polypropylene glycol, ubiquitous polyether", "Polyethylene glycol, ubiquitous polyether",
      "Butylated hydroxytoluene, Antioxidant", "N,N'-Dicyclohexylurea",
      "matrix compound, potassiated", "Polypropylene glycol, ubiquitous polyether",
      "n-butyl benzenesulfonamide, plasticizer", "Polyethylene glycol, ubiquitous polyether",
      "n-butyl benzenesulfonamide, plasticizer", "Polyethylene glycol, ubiquitous polyether",
      "Triethylamine-hydrochloride, buffer", "Triethylamine-hydrochloride, buffer",
      "Tetrabutylammonium, buffer", "Trityl cation, [Ph3C]+",
      "Butylated hytroxytoluene, Antioxidant additives",
      "Polypropylene glycol, ubiquitous polyether", "X-100, X-114, X-405, or X-45 Detergents",
      "Dimethylsulfoxide, solvent", "Polyethylene glycol, ubiquitous polyether", "101 Detergents",
      "Tributylphosphate", "Monomethoxytrityl cation", "Polypropylene glycol, ubiquitous polyether",
      "X-100, X-114, X-405, or X-45 Detergents", "Polyethylene glycol, ubiquitous polyether",
      "Triphenylphosphine oxide", "Plasticiser, phtalate ester",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Slip agent in polyethylene films",
      "Polyethylene glycol, ubiquitous polyether", "Slip agent in polyethylene films",
      "101 Detergents", "n,n-bis(2-hydroxyethyl) dodecanamide, anti-static agent",
      "Polypropylene glycol, ubiquitous polyether", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "Dibutylphthalate, plasticizer",
      "Slip agent in polyethylene films", "Polyethylene glycol, ubiquitous polyether",
      "Slip agent in polyethylene films", "Polypropylene glycol, ubiquitous polyether",
      "101 Detergents", "Dibutyl sebacate, plasticizer", "Dibutylphthalate, plasticizer",
      "X-100, X-114, X-405, or X-45 Detergents", "Polyethylene glycol, ubiquitous polyether",
      "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Diethylene glycol monobutyl ether, cpd. In scintillation cocktail",
      "Triphenyl phosphate, flame retardant in plastics",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "101 Detergents", "Tributyl tin formate, catalyst", "101R Detergents",
      "Erucamide, (Cis-13-docosenoic amide)", "X-100, X-114, X-405, or X-45 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "Polyethylene glycol, ubiquitous polyether",
      "101 Detergents", "Polysiloxane, (neutral methane loss from m/z 371)",
      "Palmitamidopropyl-trimonium chloride, personal care products additive",
      "Erucamide, (Cis-13-docosenoic amide)", "X-100, X-114, X-405, or X-45 Detergents",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Behentrimonium chloride, personal care product additive",
      "Polysiloxane, followed by m/z 388", "Polyethylene glycol, ubiquitous polyether",
      "Bis(2-ethylhexyl) adipate, plasticizer", "Dioctyl adipate, plasticizer", "101 Detergents",
      "matrix cluster, dimer", "101R Detergents", "X-100, X-114, X-405, or X-45 Detergents",
      "Polysiloxane, (see m/z 371)", "Polypropylene glycol, ubiquitous polyether",
      "Diisooctyl phthalate, plasticiser", "Polyethylene glycol, ubiquitous polyether",
      "101 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "X-100, X-114, X-405, or X-45 Detergents", "Polyethylene glycol, ubiquitous polyether",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Diisooctyl phthalate, plasticiser",
      "Polyethylene glycol, ubiquitous polyether", "101 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents",
      "Polysiloxane, (neutral methane loss from m/z 445)", "Diisooctyl phthalate, plasticiser",
      "Polyethylene glycol, ubiquitous polyether", "matrix-copper adduct, together with m/z 443",
      "101 Detergents", "matrix-copper adduct, together with m/z 443",
      "Polysiloxane, followed by m/z 462", "Polypropylene glycol, ubiquitous polyether",
      "X-100, X-114, X-405, or X-45 Detergents", "N,N'-Dicyclohexylurea",
      "Polyethylene glycol, ubiquitous polyether",
      "Cyclic oligomer of polyamide 66, (adipic acid/hexylmethylene diamine condensation) ",
      "Diisooctyl phthalate, plasticiser", "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Polyethylene glycol, ubiquitous polyether", "Polysiloxane (see m/z 445)",
      "Polypropylene glycol, ubiquitous polyether", "101 Detergents", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "porcine trypsin",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "101 Detergents", "X-100, X-114, X-405, or X-45 Detergents",
      "Dipalmityldimethylammonium chloride, catalyst, personal care products additive",
      "Polyethylene glycol, ubiquitous polyether", "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Polysiloxane, (neutral methane loss from m/z 519)",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "101 Detergents", "101R Detergents", "trypsin-like artefact",
      "X-100, X-114, X-405, or X-45 Detergents", "Didodecyl 3,3′-thiodipropionate, antioxidant",
      "Polysiloxane, followed by m/z 536", "Polypropylene glycol, ubiquitous polyether",
      "Stearyl-palmityldimethylammonium chloride, catalyst, personal care product additive",
      "Polyethylene glycol, ubiquitous polyether", "101 Detergents",
      "Didodecyl 3,3′-thiodipropionate oxidized to sulfoxide, antioxidant",
      "Irganox 1076, antioxidant in synthetic polymers, antioxidant", "Polysiloxane (see m/z 519)",
      "X-100, X-114, X-405, or X-45 Detergents", "during ESI with metal tips and acetic acid",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Polyethylene glycol, ubiquitous polyether",
      "Didodecyl 3,3′-thiodipropionate oxidized to sulfone, antioxidant",
      "Distearyldimethylammonium chloride, catalyst, personal care products additive",
      "101 Detergents", "Didodecyl 3,3′-thiodipropionate oxidized to sulfoxide, antioxidant",
      "Irganox 1076, antioxidant in synthetic polymers, antioxidant",
      "during ESI with metal tips and acetic acid", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "matrix cluster, trimer", "Polyethylene glycol, ubiquitous polyether", "porcine trypsin",
      "101 Detergents", "Polysiloxane, (neutral methane loss from m/z 593)",
      "Polypropylene glycol, ubiquitous polyether", "X-100, X-114, X-405, or X-45 Detergents",
      "Polyethylene glycol, ubiquitous polyether", "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Polyethylene glycol, ubiquitous polyether", "Polysiloxane, followed by m/z 610",
      "101 Detergents", "during ESI with metal tips and acetic acid",
      "Polypropylene glycol, ubiquitous polyether", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "matrix cluster, trimer, potassiated",
      "Polysiloxane (see m/z 593)", "Polyethylene glycol, ubiquitous polyether",
      "3-[(3-Cholamidopropyl)dimethylammonio]-1-propanesulfonate", "101 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "during ESI with metal tips and acetic acid",
      "X-100, X-114, X-405, or X-45 Detergents", "Polyethylene glycol, ubiquitous polyether",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Bovine trypsin",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "101 Detergents", "101R Detergents", "keratin (human), ion source fragment",
      "X-100, X-114, X-405, or X-45 Detergents",
      "Polysiloxane, (neutral methane loss from m/z 667)",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "Bovine trypsin", "101 Detergents", "Polysiloxane, followed by m/z 684",
      "X-100, X-114, X-405, or X-45 Detergents", "porcine trypsin", "porcine trypsin",
      "Polyethylene glycol, ubiquitous polyether", "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "Cyclic oligomer of polyamide 66, (adipic acid/hexylmethylene diamine condensation) ",
      "101 Detergents", "Polysiloxane (see m/z 667)", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "Polyethylene glycol, ubiquitous polyether", "keratin", "101 Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "Polyethylene glycol, ubiquitous polyether", "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Polyethylene glycol, ubiquitous polyether",
      "Polysiloxane, (neutral methane loss from m/z 741)", "101 Detergents",
      "bovine serum albumin (BSA)", "101R Detergents", "X-100, X-114, X-405, or X-45 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "Polysiloxane, followed by m/z 758",
      "bovine casein alpha-S1", "porcine trypsin", "Polyethylene glycol, ubiquitous polyether",
      "101 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "X-100, X-114, X-405, or X-45 Detergents", "Polysiloxane (see m/z 741)", "porcine trypsin",
      "Polyethylene glycol, ubiquitous polyether", "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Polyethylene glycol, ubiquitous polyether", "101 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "Polyethylene glycol, ubiquitous polyether",
      "101 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "Diisooctyl phthalate, plasticiser", "X-100, X-114, X-405, or X-45 Detergents",
      "porcine trypsin, truncated", "Diisooctyl phthalate, plasticiser", "keratin (human)",
      "bovine trypsin", "Polyethylene glycol, ubiquitous polyether", "keratin (human)",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "keratin", "Tween 20",
      "Polyethylene glycol, ubiquitous polyether", "Polypropylene glycol, ubiquitous polyether",
      "101 Detergents", "Diisooctyl phthalate, plasticiser", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "trypsin-like artefact", "keratin (human)",
      "keratin (human)", "Polypropylene glycol, ubiquitous polyether", "keratin (human)",
      "Polyethylene glycol, ubiquitous polyether", "101 Detergents", "matrix cluster",
      "porcine trypsin", "matrix cluster", "X-100, X-114, X-405, or X-45 Detergents",
      "keratin (human)", "Polyethylene glycol, ubiquitous polyether",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Tween 20",
      "Polypropylene glycol, ubiquitous polyether", "matrix cluster",
      "Polyethylene glycol, ubiquitous polyether", "101 Detergents", "matrix cluster",
      "keratin (human), ion source fragment", "keratin (human), ion source fragment", "Tween 40",
      "101R Detergents", "matrix cluster", "X-100, X-114, X-405, or X-45 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "porcine trypsin - methylated",
      "matrix cluster", "trypsin-like artefact", "keratin (human)",
      "Polyethylene glycol, ubiquitous polyether", "keratin (human)", "101 Detergents",
      "Angiotensin standards", "X-100, X-114, X-405, or X-45 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "Tween 80",
      "Polyethylene glycol, ubiquitous polyether", "Tween 60",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Tween 20", "trypsin-like artefact",
      "Polyethylene glycol, ubiquitous polyether", "101 Detergents",
      "Cyclic oligomer of polyamide 66, (adipic acid/hexylmethylene diamine condensation) ",
      "bovine trypsin", "porcine trypsin", "Tween 40", "101R Detergents",
      "X-100, X-114, X-405, or X-45 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "Angiotensin standards (bovine)", "Polyethylene glycol, ubiquitous polyether",
      "101 Detergents", "Polypropylene glycol, ubiquitous polyether",
      "Angiotensin standards (human)", "X-100, X-114, X-405, or X-45 Detergents", "Tween 80",
      "Polyethylene glycol, ubiquitous polyether", "Tween 60",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Tween 20", "101 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "trypsin-like artefact, truncated", "Tween 40",
      "101R Detergents", "X-100, X-114, X-405, or X-45 Detergents", "101 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "keratin (human)",
      "X-100, X-114, X-405, or X-45 Detergents", "bovine trypsin", "Tween 80", "Tween 60",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Tween 20",
      "Polypropylene glycol, ubiquitous polyether", "101 Detergents", "matrix cluster", "keratin",
      "Tween 40", "101R Detergents", "X-100, X-114, X-405, or X-45 Detergents", "keratin",
      "bovine serum albumin (BSA)", "keratin", "Polypropylene glycol, ubiquitous polyether",
      "101 Detergents", "bovine trypsin", "trypsin-like artefact, truncated",
      "X-100, X-114, X-405, or X-45 Detergents", "trypsin-like artefact, truncated",
      "bovine trypsin", "Tween 80", "Tween 60", "X-100R, X-114R, X-405R, or X-45R Detergents",
      "Polypropylene glycol, ubiquitous polyether", "Tween 20", "keratin (human)",
      "keratin (human)", "101 Detergents", "matrix cluster", "keratin", "keratin (human)",
      "Tween 40", "101R Detergents", "X-100, X-114, X-405, or X-45 Detergents",
      "Polypropylene glycol, ubiquitous polyether", "matrix cluster", "porcine trypsin",
      "bovine trypsin", "matrix cluster", "101 Detergents",
      "matrix cluster, pentamer, triply potassiated", "keratin (human)",
      "Polypropylene glycol, ubiquitous polyether", "lysyl endopeptidase (Lys-C)",
      "keratin (human)", "keratin", "X-100, X-114, X-405, or X-45 Detergents",
      "matrix cluster, pentamer, potassiated, sodiated", "keratin (human)", "Tween 80", "Tween 60",
      "keratin (human)", "X-100R, X-114R, X-405R, or X-45R Detergents", "Tween 20",
      "101 Detergents", "bovine trypsin, in source fragment", "keratin (human)", "Tween 40",
      "101R Detergents", "Polypropylene glycol, ubiquitous polyether",
      "X-100, X-114, X-405, or X-45 Detergents", "keratin (human)", "porcine trypsin",
      "101 Detergents", "Polypropylene glycol, ubiquitous polyether", "keratin (human)",
      "X-100, X-114, X-405, or X-45 Detergents", "bovine trypsin", "Tween 80", "Tween 60",
      "X-100R, X-114R, X-405R, or X-45R Detergents", "Tween 20", "keratin (human)",
      "keratin (human)", "Polypropylene glycol, ubiquitous polyether", "101 Detergents",
      "porcine trypsin, methylated, truncated", "Keratin", "Tween 40", "101R Detergents", "Keratin",
      "bovine trypsin", "Polypropylene glycol, ubiquitous polyether", "bovine trypsin", "Tween 80",
      "keratin (human)", "Tween 60", "Polypropylene glycol, ubiquitous polyether", "Tween 20",
      "keratin (human)", "bovine trypsin", "Tween 40", "bovine trypsin", "porcine trypsin",
      "keratin (human)", "Polypropylene glycol, ubiquitous polyether", "keratin (human)",
      "kereatin", "bovine trypsin", "porcine trypsin", "keratin (human)", "Tween 80",
      "bovine trypsin", "Tween 60", "Polypropylene glycol, ubiquitous polyether",
      "trypsin-like artefact", "Tween 20", "keratin (human)", "keratin (human)", "bovine trypsin",
      "Tween 40", "Polypropylene glycol, ubiquitous polyether", "trypsin-like artefact",
      "porcine trypsin", "matrix cluster, hexamer, potassiated, sodiated",
      "3-[(3-Cholamidopropyl)dimethylammonio]-1-propanesulfonate", "bovine trypsin",
      "keratin (human)", "keratin (human)", "Tween 80", "Tween 60", "bovine trypsin", "Tween 20",
      "bovine casein alpha-S1", "keratin (human)", "Tween 40", "keratin", "trypsin-like artefact",
      "keratin (human)", "bovine casein alpha-S1", "keratin (human)", "keratin (human)", "keratin",
      "keratin (human)", "Angiotensin standards (bovine)", "Tween 80", "Tween 60", "Tween 20",
      "Angiotensin standards (human)", "keratin", "keratin (human)", "keratin (human)",
      "bovine serum albumin (BSA)", "Tween 40", "keratin (human)", "bovine trypsin",
      "keratin (human)", "keratin", "trypsin-like artefact", "keratin (human)", "keratin (human)",
      "keratin (human)", "Tween 80", "trypsin-like artefact", "Tween 60", "keratin (human)",
      "Tween 20", "keratin", "keratin", "Tween 40", "keratin", "keratin (human)", "keratin (human)",
      "keratin (human)", "lysyl endopeptidase (Lys-C)", "lysyl endopeptidase (Lys-C)",
      "keratin (human)", "keratin (human)", "Tween 80", "Tween 60", "keratin (human)", "Tween 20",
      "keratin (human)", "bovine casein alpha-S1", "keratin", "keratin (human)", "Tween 40",
      "bovine serum albumin (BSA)", "keratin (human), ion-source fragment", "keratin", "keratin",
      "keratin (human)", "Tween 80", "Tween 60", "porcine trypsin, methylated", "Tween 20",
      "bovine trypsin", "keratin (human)", "Tween 40", "keratin (human)",
      "bovine serum albumin (BSA)", "keratin", "bovine serum albumin (BSA)", "keratin (human)",
      "bovine trypsin", "keratin", "Tween 80", "bovine trypsin", "Tween 60", "Tween 20",
      "keratin (human)", "keratin (human)", "keratin", "bovine serum albumin (BSA)", "Tween 40",
      "bovine trypsin", "keratin (human)", "bovine serum albumin (BSA)", "porcine trypsin",
      "keratin (human)", "bovine trypsin", "keratin (human)", "Tween 80", "Tween 60", "Tween 20",
      "bovine trypsin", "bovine trypsin", "Tween 40", "keratin", "Tween 80", "Tween 60", "Tween 20",
      "porcine trypsin, truncated", "bovine serum albumin (BSA)", "Tween 40", "keratin (human)",
      "Tween 80", "Tween 60", "keratin", "Tween 20", "Tween 40", "porcine trypsin, methylated",
      "keratin (human)", "Tween 80", "bovine serum albumin (BSA)", "Tween 60", "Tween 20",
      "keratin (human)", "Tween 40", "keratin (human)", "bovine trypsin", "Tween 80", "Tween 60",
      "keratin (human)", "Tween 20", "Tween 40", "keratin (human)", "keratin (human)",
      "porcine trypsin, truncated", "keratin (human)", "bovine trypsin", "Tween 80", "Tween 60",
      "Tween 20", "keratin (human)", "Tween 40", "bovine trypsin", "keratin (human)", "Tween 80",
      "Tween 60", "bovine trypsin", "porcine trypsin", "Tween 20", "Tween 40", "keratin (human)",
      "keratin", "keratin", "Tween 80", "Tween 60", "lysyl endopeptidase (Lys-C)", "Tween 40",
      "keratin", "keratin (human)", "keratin", "keratin", "Tween 80", "Tween 60",
      "porcine trypsin, truncated, methylated", "porcine trypsin, methylated", "keratin (human)",
      "keratin (human)", "porcine trypsin, methylated", "keratin (human)",
      "lysyl endopeptidase (Lys-C)", "keratin", "porcine trypsin, truncated", "keratin",
      "keratin (human)", "trypsin-like artefact, truncated", "bovine trypsin", "keratin", "keratin",
      "bovine trypsin", "bovine trypsin", "bovine trypsin", "bovine trypsin", "bovine trypsin",
      "bovine trypsin", "porcine trypsin", "porcine trypsin", "bovine trypsin, ion source fragment",
      "porcine trypsin", "keratin", "bovine trypsin", "bovine trypsin", "bovine trypsin",
      "bovine trypsin", "porcine trypsin", "keratin (human)", "bovine trypsin", "bovine trypsin",
      "porcine trypsin, methylated", "bovine trypsin", "bovine trypsin", "bovine trypsin",
      "keratin", "bovine trypsin", "bovine trypsin", "keratin (human)", "bovine trypsin",
      "keratin (human)", "bovine trypsin", "keratin (human)", "keratin (human)", "keratin (human)",
      "bovine trypsin", "bovine trypsin", "bovine trypsin", "bovine trypsin",
      "porcine trypsin, methylated", "keratin", "keratin (human)", "keratin", "bovine trypsin",
      "bovine trypsin", "bovine trypsin", "bovine trypsin", "bovine trypsin", "keratin",
      "porcine trypsin, methylated", "porcine trypsin, methylated", "bovine trypsin", "keratin",
      "porcine trypsin, methylated", "porcine trypsin, methylated", "keratin", "keratin", "keratin",
      "porcine trypsin, methylated", "keratin (human)", "bovine trypsin", "keratin",
      "bovine trypsin", "keratin (human)", "trypsin-like artefact, truncated",
      "porcine trypsin, methylated", "trypsin-like artefact, truncated"};

  private String[] possibleOriginNeg = new String[] {"Fragment from acetonitrile ",
      "Formic acid, FA (anion)", "Acetic Acid, Ac (anion)",
      "Phosphoric acid (phosphate, anion) also from oligonucleotides or phosphopeptides",
      "Sulfuric acid (sulfate, anion) also from other sulfated materials",
      "Methanesulfuric acid (methanesulfonate, anion)", "Sulfuric acid (sulfate, anion)",
      "Phosphoric acid (phosphate, anion) also from oligonucleotides or phosphopeptides",
      "Trifluoroacetic acid, TFA (anion)", "Formic acid, dimer, FA (anion)",
      "Iodine anion (Iodide)", "Pentafluoropropionic acid, Perfluoropropionic acid",
      "n-butyl benzenesulfonamide", "Trifluoroacetic acid, TFA (anion)",
      "Trifluoroacetic acid, TFA, sodiated (anion)", "Palmitic acid (carboxylate anion) C16:0",
      "Oleic acid (carboxylate anion) C18:1", "Stearic acid (carboxylate anion) 18:0",
      "Copper catalyst iodide adduct", "Copper catalyst iodide adduct"};

  /**
   * @param dataFile
   * @param parameters
   */
  public KellerListTask(MZmineProject project, RawDataFile dataFile, ParameterSet parameters) {

    this.parameters = parameters;

  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  public String getTaskDescription() {
    return "Building EICs for contaminantes tables";
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
   */
  public double getFinishedPercentage() {
    if (totalSteps == 0)
      return 0;
    else
      return (double) processedSteps / totalSteps;
  }

  /**
   * @see Runnable#run()
   */
  public void run() {
    setStatus(TaskStatus.PROCESSING);
    // Task canceled?
    if (isCanceled())
      return;

    this.dataFile = KellerListParameters.dataFiles.getValue().getMatchingRawDataFiles()[0];
    this.scanSelection = parameters.getParameter(KellerListParameters.scanSelection).getValue();
    this.mzTolerance = parameters.getParameter(KellerListParameters.mzTolerance).getValue();
    TableModel model = new DefaultTableModel(new Object[][] {},
        new String[] {"<html>Monoisotopic <br>ion mass <br>(singly charged)<html>",
            "Extracted ion chromatogram", "Ion type",
            "<html>Formula for M<br>or subunit or<br>sequence<html>",
            "<html>Compound ID<br> or species<html>",
            "<html>Possible origin<br>and other<br>comments<html>"}) {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public boolean isCellEditable(int row, int col) {
        if (col == 1)
          return false;
        else
          return true;
      }
    };

    scans = scanSelection.getMatchingScans(dataFile);
    // check polarity
    for (

        int i = 1; i < scans.length; i++) {
      if (scans[i].getPolarity() == PolarityType.POSITIVE) {
        hasPositivePolarity = true;
        totalSteps = monoIsoMassesPos.length;
      }
      if (scans[i].getPolarity() == PolarityType.NEGATIVE) {
        hasNegativePolarity = true;
        totalSteps = monoIsoMassesNeg.length;
      }
      if (hasPositivePolarity == true && hasNegativePolarity == true) {
        totalSteps = monoIsoMassesPos.length + monoIsoMassesNeg.length;
        break;
      }
    }

    KellerListCellRenderer rendererEic = new KellerListCellRenderer();
    Range<Double> mzRange = dataFile.getDataMZRange();
    // Add positive ions
    if (hasPositivePolarity) {
      for (int i = 0; i < monoIsoMassesPos.length; i++) {
        // Set mass range filter
        if (mzRange.contains(monoIsoMassesPos[i])) {
          Range<Double> range = mzTolerance.getToleranceRange(monoIsoMassesPos[i]);
          KellerListDataset dataSet =
              new KellerListDataset(dataFile, monoIsoMassesPos[i], range, noiseLevel, scans);
          // If intensity is 0, don't add row
          if (dataSet.intensityOverZero()) {
            JLabel label = null;
            label = new JLabel(new ImageIcon((getEicChart(dataSet).createBufferedImage(300, 100))));
            rendererEic.lbl.add(label);
            rendererEic.setSize(label.getSize());
            ((DefaultTableModel) model)
                .addRow(new Object[] {numberFormat.format(monoIsoMassesPos[i]), label,
                    ionTypePos[i], formulaPos[i], compoundIDPos[i], possibleOriginPos[i]});
            logger.info("Number " + i + " of " + monoIsoMassesPos.length + " Building EIC of: "
                + compoundIDPos[i]);
          }
        }
        processedSteps++;
      }
    }

    // Add negative ions
    if (hasNegativePolarity) {
      for (int i = 0; i < monoIsoMassesNeg.length; i++) {
        // Set mass range filter
        if (mzRange.contains(monoIsoMassesNeg[i])) {
          Range<Double> range = mzTolerance.getToleranceRange(monoIsoMassesNeg[i]);
          KellerListDataset dataSet =
              new KellerListDataset(dataFile, monoIsoMassesNeg[i], range, noiseLevel, scans);
          // If intensity is 0, dont add row
          if (dataSet.intensityOverZero()) {
            JLabel label = null;
            label = new JLabel(new ImageIcon((getEicChart(dataSet).createBufferedImage(300, 75))));
            rendererEic.lbl.add(label);
            rendererEic.setSize(label.getSize());
            ((DefaultTableModel) model).addRow(new Object[] {
                numberFormat.format(monoIsoMassesNeg[i]), label, ionTypeNeg[i], formulaNeg[i], " ", //
                possibleOriginNeg[i]});
            logger.info(
                "Number " + i + " of  " + monoIsoMassesNeg.length + " Building EIC of: " + " ");
          }
        }
        processedSteps++;
      }
    }

    KellerListTableFrame tableFrame =
        new KellerListTableFrame(dataFile.getName(), (DefaultTableModel) model, rendererEic);

    tableFrame.setVisible(true);
    tableFrame.validate();

    logger.info("Finished");

    setStatus(TaskStatus.FINISHED);
  }

  private JFreeChart getEicChart(KellerListDataset dataset) {
    JFreeChart chart = ChartFactory.createXYLineChart("", // title
        "Retention time [min]", // x-axis label
        null, // y-axis label
        dataset, // data set
        PlotOrientation.VERTICAL, // orientation
        false, // create legend?
        false, // generate tooltips?
        false // generate URLs?
    );
    XYPlot plot = (XYPlot) chart.getPlot();
    ChartPanel chartPanel = new ChartPanel(chart);
    chart.setBackgroundPaint(Color.white);
    chartPanel.setChart(chart);

    // Disable maximum size (we don't want scaling).
    chartPanel.setMaximumDrawWidth(Integer.MAX_VALUE);
    chartPanel.setMaximumDrawHeight(Integer.MAX_VALUE);

    // Set the plot properties.
    plot = chart.getXYPlot();
    plot.setBackgroundPaint(Color.white);
    plot.getRenderer().setSeriesPaint(0, Color.black);
    plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 12));
    NumberFormat numberFormat = new DecimalFormat("0.#E0");
    NumberAxis yAxis = new NumberAxis("Intensity");
    yAxis.setNumberFormatOverride(numberFormat);
    yAxis.setLabelFont(new Font("Arial", Font.PLAIN, 12));
    plot.setRangeAxis(yAxis);

    // Set grid properties.
    plot.setDomainGridlinePaint(Color.white);
    plot.setRangeGridlinePaint(Color.white);
    plot.setOutlineVisible(false);

    return chart;
  }

}

