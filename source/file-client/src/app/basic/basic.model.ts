/**
 * 树节点
 */
export class Node {
  public node: string; // 节点
  public parentNode: string; // 父节点
  public seq?: number; // 排序
  public isLeaf?: boolean; // 是否是子节点
  public children?: Array<Node>; // 子节点集合
  public data?: any; // 其他数据
}

