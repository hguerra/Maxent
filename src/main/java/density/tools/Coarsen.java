/*
Copyright (c) 2016 Steven Phillips, Miro Dudik and Rob Schapire

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions: 

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software. 

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
*/

package density.tools;

import density.Grid;
import density.GridDimension;
import density.GridIO;
import density.GridWriter;

import java.io.File;
import java.io.IOException;

public class Coarsen {

    public static void main(String args[]) {
        try {
            new Coarsen().go(args);
        } catch (IOException e) {
            System.out.println("Error: " + e.toString());
            System.exit(0);
        }
    }

    void go(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: Coarsen radius outdir file1 file2...");
            System.exit(0);
        }
        final int radius = Integer.parseInt(args[0]);
        final int reduce = 2 * radius + 1;
        String outdir = args[1];
        for (int i = 2; i < args.length; i++) {
            String infile = args[i];
            String name = new File(infile).getName();
            System.out.println(name);
            final Grid old = GridIO.readGrid(infile);
            GridDimension dim = old.getDimension();
            int rr = (int) Math.ceil(dim.getnrows() / (double) reduce);
            int cc = (int) Math.ceil(dim.getncols() / (double) reduce);
            GridDimension newdim = new GridDimension(dim.getxllcorner(), dim.getyllcorner(), dim.getcellsize() * reduce, rr, cc);
            Grid newg = new Grid(newdim, "Coarsened") {
                public boolean hasData(int r, int c) {
                    for (int rr = r * reduce; rr < (r + 1) * reduce; rr++)
                        for (int cc = c * reduce; cc < (c + 1) * reduce; cc++)
                            if (old.hasData(rr, cc))
                                return true;
                    return false;
                }

                public float eval(int r, int c) {
                    double sum = 0;
                    for (int rr = r * reduce; rr < (r + 1) * reduce; rr++)
                        for (int cc = c * reduce; cc < (c + 1) * reduce; cc++)
                            if (old.hasData(rr, cc))
                                sum += old.eval(rr, cc);
                    return (float) sum;
                }
            };
            new GridWriter(newg, new File(outdir, name)).writeAll();
        }
    }
}
