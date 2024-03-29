# Linear Algebra Calculator

Zou Shanghao 1235425

---
## Project Requirements
- Calculate linear algebra problems, including determinant, matrix, vector and linear equations
- Integrate all calculators into one console
- Eliminate floating point precision issues
- Support fraction & square root input/output

---
## Requirements Analysis
- Create separate classes for each kind of calculation, provide calculation methods 
- Use the console as the user interface, use the calculation methods from other classes
- Create custom Fraction class and Real class to handle all numbers

---
## Classes Diagram
![Classes Diagram.png](Classes%20Diagram.png)

---
## Actors
- A console which deals with the user
- All sorts of calculators
- A fraction class encapsulating BigDecimals
- A real number class encapsulating fraction classes
- A utility class providing some useful methods

---
## Det.class

![Det Process.png](Det%20Process.png)

---
## Mat.class & Vec.class

Contains each method for every specific mathematical operation

---
## LinearEquation.class

![LinearEquation Process.png](LinearEquation%20Process.png)

---
## Fraction.class
This class encapsulates BigDecimals and eliminates floating point precision issues, 
and also allows users to input any form of fraction, providing simplified fraction output.

![Fraction Process.png](Fraction%20Process.png)

---
## Real.class
Fraction class is not enough for vector operations; 
this class encapsulates Fraction class to provide real number support.

![Real Process.png](Real%20Process.png)

---
## Tool.class
Simply a utility class for some frequently used methods among all classes.

---
## Use case description
1. User opens the console, type corresponding commands to access a specific calculator
2. The calculator guides the user for further info, such as the input of a matrix
3. After providing all values needed, the calculator then quickly outputs the result
4. The calculator returns the program to the console, allowing another access
