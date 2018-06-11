remove.outliers <- function(x, conf.level = 0.95)
{
  x <- x[!is.na(x)]
  del.val <- NULL
  
  while (TRUE) {
    n <- length(x)
    if (n < 3) {
      break
    }
    
    
    r <- range(x)
    if(r[1] == r[2])
    	break;
    	
    t <- abs(r - mean(x)) / sd(x)
    q <- sqrt((n - 2) / ((n - 1) ^ 2 / t ^ 2 / n - 1))
    if(is.nan(q[1]) || is.nan(q[2]))
    	break;
    p <- n * pt(q, n - 2, lower.tail = FALSE)
    
    if (t[1] < t[2]) {
      if (p[2] < 1 - conf.level) {
        del.val <- c(del.val, r[2])
        x <- x[x != r[2]]
        next
      }
    } else {
      if (p[1] < 1 - conf.level) {
        del.val <- c(del.val, r[1])
        x <- x[x != r[1]]
        next
      }
    }
    break
  }
  return(list(x = x, del.val = del.val))
}
