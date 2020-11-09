package parser;

public class Action {
  public Act action;
  //if action = shift : number is state
  //if action = reduce : number is number of rule
  public int number;

  public Action(Act action, int number) {
    this.action = action;
    this.number = number;
  }

  public String toString() {
    if (action.equals(Act.accept)) {
      return "acc";
    }
    return action.toString().substring(0, 1) + number;
  }
}

enum Act {
  shift,
  reduce,
  accept
}
