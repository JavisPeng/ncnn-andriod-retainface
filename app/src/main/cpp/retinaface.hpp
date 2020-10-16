#include "net.h"

/**************************************opencv code replaced************************************/
template<typename T>
class Rect {
public:
    T x, y, width, height;

     T area() const { return width * height; }

    static T intersection_area(const Rect<T> &a, const Rect<T> &b){
        T x0 = std::max(a.x, b.x), y0 = std::max(a.y, b.y);
        T x1 = std::min(a.x + a.width, b.x + b.width), y1 = std::min(a.y + a.height, b.y + b.height);
        return (x1 - x0) * (y1 - y0);
    }
};

struct Point2f {
    float x, y;
};
/**************************************opencv code replaced************************************/

struct FaceObject {
    Rect<float> rect;
    Point2f landmark[5];
    float prob;
};

extern std::vector<FaceObject> detect_retinaface(const ncnn::Net & retinaface,ncnn::Mat in);
