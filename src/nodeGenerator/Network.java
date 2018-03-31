package nodeGenerator;

import nodeGenerator.field.Field;
import nodeGenerator.generatorException.NodeExistException;
import nodeGenerator.generatorException.NodeRelationsCountException;
import nodeGenerator.generatorException.OneselfConnection;
import nodeGenerator.generatorException.OutOfFieldException;

import java.util.ArrayList;
import java.util.List;


public class Network {
    private NetworkType Type;
    private List<Node> Nodes;
    private int MaxNodeCount;



    public Node GetLastNode(){
        return Nodes.get(Nodes.size() - 1);
    }
    public void setMaxNodeRelations(int maxNodeRelations) throws Exception {
        for(Node t: Nodes){
            t.setMaxRelationsCount(maxNodeRelations);
        }
    }
    public void setMaxNodeCount(int maxNodeCount) {
        MaxNodeCount = maxNodeCount;
    }

    public Node GetByCoord(int x, int y){
        for (Node t: Nodes){
            if(t.getCellNumber_X() == x)
                if(t.getCellNumber_Y() == y)
                    return t;
        }
        return null;
    }

    public boolean CheckID(int ID){
        boolean entered = false;
        for(Node t: Nodes) {
            int t_ID = t.getID();
            if(t_ID == ID)
                entered = true;
        }
       return entered;
    }

    public nodeGenerator.Node GetNodeByID(int ID){
        for(Node t: Nodes)
            if(t.getID() == ID)
                return t;
         return null;
    }
    public boolean isAllHaveMaxRelations(){
        for (Node t: Nodes){
            if(t.getRelationsCount() < t.getMaxRelationsCount())
                return false;
        }
        return true;
    }

    public boolean CheckIntersection(Node from, Node to){ //check
        int t_x = Math.abs(from.getCellNumber_X() - to.getCellNumber_X());
        int t_y = Math.abs(from.getCellNumber_Y() - to.getCellNumber_Y());
        if(t_x == 0){
            int start = Math.min(from.getCellNumber_Y(), to.getCellNumber_Y());
            for (int i = 1; i < t_y; i++){
                if(GetByCoord(from.getCellNumber_X(), start + i) != null)
                    return true;
            }
            return false;
        }
        else
            if(t_y == 0){
            int start = Math.min(from.getCellNumber_X(), to.getCellNumber_X());
            for (int i = 1; i < t_x; i++){
                if(GetByCoord(start + i, from.getCellNumber_Y()) != null)
                    return true;
            }
            return false;
        }
        else
            if(t_x == t_y) {
                int start = Math.min(from.getCellNumber_X(), to.getCellNumber_X());
                for (int i = 1; i < t_x; i++){
                    if(GetByCoord(start + i, start + i) != null)
                        return true;
                }
                return false;
        }
        return false;
    }

    public void CreateParentNode(int CellNumber_X, int CellNumber_Y) throws Exception {
        if(!Nodes.isEmpty())
            throw new NodeExistException("Parent Node is already exist");
        if(CellNumber_X < 0 || CellNumber_Y < 0
                || CellNumber_X >= Field.getInstance().getCellsCount()
                || CellNumber_Y >= Field.getInstance().getCellsCount())
            throw new OutOfFieldException("Out from field borders", CellNumber_X , CellNumber_Y);
        Nodes.add(new Node(Type, CellNumber_X, CellNumber_Y, 0));

    }

    /**между соедияемыми не должно быть других узлов. Иначе соединение проигнорируется*/
    /*public boolean AddNode(Direction direction, int parentNodeID, List<Integer> connectWith) throws Exception {
        if(Nodes.isEmpty())
            throw new Exception("You must create parent node firstly");
        if(direction == Direction.None)
            throw new Exception("Node must have direction");
        if(Nodes.size() + 1 > MaxNodeCount)
            throw new NodeRelationsCountException("Max node counts");
        if(!CheckID(parentNodeID))
            throw new Exception("Node with ID " + parentNodeID + " are not exist in this network");

        boolean newNode = true;
        Node parentNode = GetNodeByID(parentNodeID);

        int ID;
        Node nodeByDirection = parentNode.GetNodeByDirection(direction);
        int cell_X = Direction.Check_X_by_Direction(parentNode, direction);
        if(cell_X < 0 || cell_X >= Field.getInstance().getCellsCount())
            throw new OutOfFieldException("Out from field borders. Horizontal cell index is " + cell_X);
        int cell_Y = Direction.Check_Y_by_Direction(parentNode, direction);
        if(cell_Y < 0 || cell_Y >= Field.getInstance().getCellsCount())
            throw new OutOfFieldException("Out from field borders. Vertical cell index is " + cell_Y);

        if(nodeByDirection == null)
        {
            ID = Nodes.get(Nodes.size() - 1).getID() + 1;
            Nodes.add(new Node(Type, cell_X, cell_Y, ID));
        }
        else
        {
            ID = nodeByDirection.getID();
            newNode = false;
        }

        Node lastAdded = Nodes.get(ID);

        try {
            parentNode.ConnectNode(lastAdded, direction);
        }
        catch (NodeRelationsCountException e){
            if(newNode)
                Nodes.remove(lastAdded);
            connectWith = null;
        }

        if(connectWith != null) {
            for (int t : connectWith) {
                Node ConnectingNode = GetNodeByID(t);
                if (ConnectingNode != null) {
                    if (CheckIntersection(lastAdded, ConnectingNode))
                        continue;
                    Direction t_direction = Direction.CheckDirection(lastAdded, ConnectingNode);
                    try {
                        if (t_direction != null) {
                            lastAdded.ConnectNode(ConnectingNode, t_direction);
                        }
                    }
                    catch(OneselfConnection e) {
                        continue;
                    } catch (NodeRelationsCountException e){
                        continue;
                    }
                }
            }
        }

        return newNode;
    }
*/
    public void addNode(int x, int y, List<Integer> connectWith) throws NodeRelationsCountException, NodeExistException {
        if(Nodes.size() + 1 > MaxNodeCount) {
            throw new NodeExistException("Максимальное количество узлов в этой сети уже достигнуто");
        }

        if(GetByCoord(x, y) != null){
            throw new NodeExistException("В этой ячейке уже существует узел");
        }
        int ID;
        if(Nodes.isEmpty()) {
            ID = 0;
        }
        else {
            ID = Nodes.get(Nodes.size() - 1).getID() + 1;
        }

        Nodes.add(new Node(Type, x, y, ID));

        Node lastAdded = Nodes.get(ID);

        for (int t : connectWith) {
            Node ConnectingNode = GetNodeByID(t);
            if (ConnectingNode != null) {
                /*if (CheckIntersection(lastAdded, ConnectingNode)){
                    continue;
                }*/
                try {
                    lastAdded.connectNode(ConnectingNode);
                }
                catch(OneselfConnection | NodeRelationsCountException e) {}
            }
        }
    }

    public int Size(){
        return Nodes.size();
    }

    public Network() {
        Nodes = new ArrayList<>();
        MaxNodeCount = 8965545;
    }

    public Network(NetworkType type, int maxNodeCount) {
        MaxNodeCount = maxNodeCount;
        Nodes = new ArrayList<>();
        Type = type;
    }

    public NetworkType getType() {
        return Type;
    }

    public void setType(NetworkType type) {
        Type = type;
    }

    public List<nodeGenerator.Node> getNodes() {
        return Nodes;
    }

    public void setNodes(List<Node> nodes) {
        Nodes = nodes;
    }
}
