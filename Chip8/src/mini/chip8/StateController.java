package mini.chip8;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import mini.chip8.Chip8.State;
import mini.chip8.Chip8.StateListener;

public class StateController implements StateListener {
	//@formatter:off
	@FXML private TextField pcTextField;
	@FXML private TextField iTextField;
	@FXML private TextField delayTextField;
	@FXML private TextField soundTextField;
	@FXML private ListView<Integer> regList;
	@FXML private ListView<String> stackList;
	@FXML private ListView<Boolean> keyList;
	//@formatter:on

	@Override
	public void update(State s) {
		pcTextField.textProperty().set(String.format("0x%03X", s.s_pc));
		iTextField.textProperty().set(String.format("0x%04X", s.s_opcode));
		delayTextField.textProperty().set(String.format("%03d", s.s_delayTimer));
		soundTextField.textProperty().set(String.format("%03d", s.s_soundTimer));
		
		ObservableList<Integer> regs = FXCollections.<Integer>observableArrayList();
		for(int v : s.s_regs) {
			regs.add(v);
		}
		regList.setItems(regs);
		
		ObservableList<String> stack = FXCollections.<String>observableArrayList();
		for(int v : s.s_stack) {
			stack.add(String.format("0x%03X", v));
		}
		stackList.setItems(stack);
		stackList.getSelectionModel().clearAndSelect(s.s_sp);
	
		ObservableList<Boolean> keys = FXCollections.<Boolean>observableArrayList();
		for(boolean v : s.s_keys) {
			keys.add(v);
		}
		keyList.setItems(keys);
	}
}
