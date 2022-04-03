import javax.swing.DefaultListModel;
import org.junit.jupiter.api.*;
// import org.graalvm.compiler.debug.Assertions;

class WordOccurenceTests {

	@DisplayName("Check poem generation")
	@Test
	void shouldGeneratePoem() {
		Assertions.assertEquals(1090, ModuleSixAssignment.generate_poem("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").length);
	}
	
	
	@DisplayName("Check list model")
	@Test
	void shouldPopulateListModel() {
		DefaultListModel<String> listModelTest = new DefaultListModel<String>();
		listModelTest.addElement("1: the - 56");
		listModelTest.addElement("2: and - 38");
		listModelTest.addElement("3: i - 32");
		ModuleSixAssignment.populateListModel(3);
		for (int x = 0; x < listModelTest.size(); x++) {
			Assertions.assertEquals(listModelTest.get(x), ModuleSixAssignment.listModel.get(x));
		}
	}

}
