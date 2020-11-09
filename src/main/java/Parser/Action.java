package Parser;

public class Action {
  public act action;
  //if action = shift : number is state
  //if action = reduce : number is number of rule
  public int number;

  public Action(act action, int number) {
    this.action = action;
    this.number = number;
  }

  public String toString() {
    if (action.equals(act.accept)) {
      return "acc";
    }
    return action.toString().substring(0, 1) + number;
  }
}

enum act {
  shift,
  reduce,
  accept
}
