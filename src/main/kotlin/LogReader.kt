import java.lang.*;
import java.util.*;
import java.io.*;


class FileSizes
{
    public var oldsize: Long = 0;
    public var nochgfor: Int = 0;

    public constructor(isnew: Boolean)
    {
        nochgfor = if ( isnew ) 0 else 50000
    }
}

public class LogReader
{

    fun main(args: Array<String>) : Unit
    {
        val knownfiles = HashMap<String, FileSizes>();

        var firstrun = true;

        while(true)
        {
            Thread.sleep(100);

            for ( arg in args)
            {
                for ( file in File(arg).listFiles())
                {
                    val fname = file.getAbsolutePath();

                    if (!file.isFile())
                        continue;

                    if (!knownfiles.containsKey(fname))
                    {
                        knownfiles.put(fname, FileSizes(!firstrun));
                    }

                    val current: FileSizes = knownfiles.get(fname)!!;

                    var newsize = file.length();

                    if (current.oldsize == newsize && current.nochgfor >50)
                    {
                        // nothing happened
                        current.nochgfor++;
                        continue;
                    }

                    if(current.oldsize==newsize)
                    {
                        try
                        {
                            val lengther = RandomAccessFile(file, "r");
                            val expnewsize = lengther.length();

                            newsize = expnewsize;
                            lengther.close();
                        }
                        catch ( p :Exception)
                        {
                            current.nochgfor = 1000;
                        }
                    }

                    if(current.oldsize==newsize)
                    {
                        // nothing happened
                        current.nochgfor++;
                        continue;
                    }


                    current.nochgfor=0;

                    if(!firstrun)
                    {
                        try
                        {
                            // has changed but now stable
                            val rdr = RandomAccessFile(file, "r");

                            try
                            {
                                rdr.seek(current.oldsize);

                                val data = ByteArray((newsize - current.oldsize) as Int);
                                rdr.read(data, 0, data.size);

                                System.out.printf("%s:", fname);

                                for ( b in data)
                                    System.out.print(b as Char);
                            }
                            finally
                            {
                                rdr.close();
                            }
                        } catch (ex: Exception)
                        {
                            System.out.printf("Cant Read: %s\n", fname);
                        }
                    }
                    current.oldsize = newsize;
                }
            }

            firstrun = false;
        } // for (;;)
    }
}
