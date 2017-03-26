# Segmented Least Squares

Least squares:
----------------------------------------------------------------------------------
- Foundational problem in statistic and numerical analysis.
- Given n points in the plane:  (x1, y1), (x2, y2) , . . . , (xn, yn).
- Find a line y = ax + b that minimizes the sum of the squared error.

Segmented least squares:
----------------------------------------------------------------------------------
- Points lie roughly on a sequence of several line segments.
- Given n points in the plane (x1, y1), (x2, y2) , . . . , (xn, yn) with 
  x1 < x2 < ... < xn, find a sequence of lines that minimizes:
  - the sum of the sums of the squared errors E in each segment
  - the number of lines L
- Tradeoff function:  E + c L, for some constant c > 0.
