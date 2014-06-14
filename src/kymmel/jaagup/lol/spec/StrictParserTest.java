package kymmel.jaagup.lol.spec;

import junit.framework.TestCase;
import kymmel.jaagup.lol.spec.domain.ByteInputStream;
import kymmel.jaagup.lol.spec.util.FileUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class StrictParserTest extends TestCase {

    private File file;

    public StrictParserTest(File file) {
        this.file = file;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> loadFiles() {
        List<Object[]> data = new ArrayList<Object[]>();
        File[] files = new File("analysis/keyframes/").listFiles();
        for(File file : files) {
            data.add(new Object[] {file});
        }
        return data;
    }

    @Test
    public void testParseKeyframe() throws Exception {
        ByteInputStream stream = new ByteInputStream(FileUtil.readFileBytes(file.getCanonicalPath()));
        StrictParser.parseKeyframe(stream);
    }

}