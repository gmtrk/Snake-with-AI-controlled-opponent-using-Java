public class Point {
    public int x;
    public int y;

    public Point(int _x, int _y)
    {
        x = _x;
        y = _y;
    }

    public int getDistance(Point pt)
    {
        return Math.abs(x - pt.x) + Math.abs(y - pt.y);
    }

    public Point move(char direction)
    {
        Point p = new Point(x, y);
        if (direction == 'L')
            p.x -= 1;
        else if (direction == 'R')
            p.x += 1;
        else if (direction == 'U')
            p.y -= 1;
        else if (direction == 'D')
            p.y += 1;
        return p;
    }
}
