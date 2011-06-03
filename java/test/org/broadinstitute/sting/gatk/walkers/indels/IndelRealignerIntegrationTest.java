package org.broadinstitute.sting.gatk.walkers.indels;

import org.broadinstitute.sting.WalkerTest;
import org.broadinstitute.sting.utils.exceptions.UserException;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IndelRealignerIntegrationTest extends WalkerTest {

    private static final String mainTestBam = validationDataLocation + "indelRealignerTest.pilot1.ceu.fixed.bam";
    private static final String mainTestIntervals = validationDataLocation + "indelRealignerTest.pilot1.ceu.intervals";
    private static final String knownIndels = validationDataLocation + "indelRealignerTest.pilot1.ceu.vcf";
    private static final String baseCommandPrefix = "-T IndelRealigner -noPG -R " + b36KGReference + " -I " + mainTestBam + " -targetIntervals " + mainTestIntervals + " -compress 0 -L 20:49,500-55,500 ";
    private static final String baseCommand = baseCommandPrefix + "-o %s ";
    private static final String base_md5 = "d7c7acd346ee4c8d34f1e2499ff7c313";

    @Test
    public void testDefaults() {

        WalkerTestSpec spec1 = new WalkerTestSpec(
                baseCommand,
                1,
                Arrays.asList(base_md5));
        executeTest("test realigner defaults", spec1);

        WalkerTestSpec spec2 = new WalkerTestSpec(
                baseCommand + "-B:indels,vcf " + knownIndels,
                1,
                Arrays.asList(base_md5));
        executeTest("test realigner defaults with VCF", spec2);

        WalkerTestSpec spec3 = new WalkerTestSpec(
                baseCommand + "-D " + GATKDataLocation + "dbsnp_129_b36.rod",
                1,
                Arrays.asList(base_md5));
        executeTest("realigner defaults with dbsnp", spec3);

    }

    @Test
    public void testKnownsOnly() {
        WalkerTestSpec spec1 = new WalkerTestSpec(
                baseCommand + "-knownsOnly -B:indels,vcf " + knownIndels,
                1,
                Arrays.asList("3dd5d2c9931b375455af0bff1a2c4888"));
        executeTest("realigner known indels only from VCF", spec1);

        WalkerTestSpec spec2 = new WalkerTestSpec(
                baseCommand + "-knownsOnly -D " + GATKDataLocation + "dbsnp_129_b36.rod",
                1,
                Arrays.asList("78850024ac9ff3ba51b6f097c7041c1d"));
        executeTest("realigner known indels only from dbsnp", spec2);
    }

    @Test
    public void testLods() {
        HashMap<String, String> e = new HashMap<String, String>();
        e.put( "-LOD 60", base_md5 );
        e.put( "-LOD 1",  "f158f18198cf48bbb3d4f1b7127928a3" );

        for ( Map.Entry<String, String> entry : e.entrySet() ) {
            WalkerTest.WalkerTestSpec spec = new WalkerTest.WalkerTestSpec(
                    baseCommand + entry.getKey(),
                    1,
                    Arrays.asList(entry.getValue()));
            executeTest(String.format("realigner [%s]", entry.getKey()), spec);
        }
    }

    @Test
    public void testLongRun() {
        WalkerTestSpec spec = new WalkerTestSpec(
                "-T IndelRealigner -noPG -R " + b36KGReference + " -I " + validationDataLocation + "NA12878.chrom1.SLX.SRP000032.2009_06.bam -L 1:10,000,000-11,000,000 -targetIntervals " + validationDataLocation + "indelRealignerTest.NA12878.chrom1.intervals -compress 0 -o %s",
                1,
                Arrays.asList("be859f9a98d738becee0526887cae42e"));
        executeTest("realigner long run", spec);
    }

    @Test
    public void testNoTags() {
        WalkerTestSpec spec = new WalkerTestSpec(
                baseCommand + "--noOriginalAlignmentTags",
                1,
                Arrays.asList("e77e59cc6363cf58f392ce5ea8d7e0b6"));
        executeTest("realigner no output tags", spec);
    }

    @Test
    public void testStats() {
        WalkerTestSpec spec1 = new WalkerTestSpec(
                baseCommandPrefix + "-stats %s -o /dev/null",
                1,
                Arrays.asList("ed5a207ddf5bdda4bb76899fb3eae35c"));
        executeTest("realigner stats", spec1);

        WalkerTestSpec spec2 = new WalkerTestSpec(
                baseCommandPrefix + "-LOD 60 -stats %s -o /dev/null",
                1,
                Arrays.asList("ffab7d9ca19daa8a21e0b8f0072d39e9"));
        executeTest("realigner stats", spec2);
    }

    @Test
    public void testMaxReadsInMemory() {
        HashMap<String, String> e = new HashMap<String, String>();
        e.put( "--maxReadsInMemory 10000", "d7c7acd346ee4c8d34f1e2499ff7c313" );
        e.put( "--maxReadsInMemory 40000",  base_md5 );

        for ( Map.Entry<String, String> entry : e.entrySet() ) {
            WalkerTest.WalkerTestSpec spec = new WalkerTest.WalkerTestSpec(
                    baseCommand + entry.getKey(),
                    1,
                    Arrays.asList(entry.getValue()));
            executeTest(String.format("realigner [%s]", entry.getKey()), spec);
        }
    }
}
